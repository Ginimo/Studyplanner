package ch.ost.mas_se.oop.studyplanner.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.ost.mas_se.oop.studyplanner.exception.CyclicDependencyException;
import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.repository.ScheduleRepository;
import ch.ost.mas_se.oop.studyplanner.repository.StudyModuleRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

  private static final String PROGRAM_ID = "prog-1";

  @Mock private StudyModuleRepository studyModuleRepository;
  @Mock private ScheduleRepository scheduleRepository;

  @InjectMocks private ScheduleServiceImpl testee;

  private static StudyModule module(String key, String... prereqs) {
    return new StudyModule(key, PROGRAM_ID, "Name-" + key, null, 4, List.of(prereqs), null, null);
  }

  // --- calculateSchedule (package-private algorithm, accessible from same package) ---

  @Test
  void calculateSchedule_noModules_returnsScheduleWithNoSemesters() {
    Schedule result = testee.calculateSchedule(List.of());

    assertThat(result.getSemesters()).isEmpty();
    assertThat(result.getTotalSemesters()).isZero();
  }

  @Test
  void calculateSchedule_allIndependent_placesAllModulesInFirstSemester() {
    Schedule result = testee.calculateSchedule(List.of(module("A"), module("B"), module("C")));

    assertThat(result.getTotalSemesters()).isEqualTo(1);
    assertThat(result.getSemesters().get(0).getStudyModuleKeys())
        .containsExactlyInAnyOrder("A", "B", "C");
  }

  @Test
  void calculateSchedule_linearChain_placesEachModuleInSequentialSemester() {
    // A → B → C
    Schedule result =
        testee.calculateSchedule(List.of(module("A"), module("B", "A"), module("C", "B")));

    assertThat(result.getTotalSemesters()).isEqualTo(3);
    assertThat(result.getSemesters().get(0).getStudyModuleKeys()).containsExactly("A");
    assertThat(result.getSemesters().get(1).getStudyModuleKeys()).containsExactly("B");
    assertThat(result.getSemesters().get(2).getStudyModuleKeys()).containsExactly("C");
  }

  @Test
  void calculateSchedule_diamondDependency_groupsCorrectly() {
    // A → B, A → C, B + C → D
    Schedule result =
        testee.calculateSchedule(
            List.of(module("A"), module("B", "A"), module("C", "A"), module("D", "B", "C")));

    assertThat(result.getTotalSemesters()).isEqualTo(3);
    assertThat(result.getSemesters().get(0).getStudyModuleKeys()).containsExactly("A");
    assertThat(result.getSemesters().get(1).getStudyModuleKeys())
        .containsExactlyInAnyOrder("B", "C");
    assertThat(result.getSemesters().get(2).getStudyModuleKeys()).containsExactly("D");
  }

  @Test
  void calculateSchedule_cyclicDependency_throwsCyclicDependencyException() {
    // A → B → A
    assertThatThrownBy(() -> testee.calculateSchedule(List.of(module("A", "B"), module("B", "A"))))
        .isInstanceOf(CyclicDependencyException.class)
        .hasMessageContaining("A")
        .hasMessageContaining("B");
  }

  @Test
  void calculateSchedule_danglingPrerequisiteNotInList_isIgnoredAndModuleIsUnblocked() {
    // "MISSING" is not in the module list — should not block OO
    Schedule result = testee.calculateSchedule(List.of(module("OO", "MISSING")));

    assertThat(result.getTotalSemesters()).isEqualTo(1);
    assertThat(result.getSemesters().get(0).getStudyModuleKeys()).containsExactly("OO");
  }

  @Test
  void calculateSchedule_mixedDependenciesAndIndependent_correctLayering() {
    // OO (none), Math (none), DB (OO), AD (OO), Thesis (DB, AD)
    Schedule result =
        testee.calculateSchedule(
            List.of(
                module("OO"),
                module("Math"),
                module("DB", "OO"),
                module("AD", "OO"),
                module("Thesis", "DB", "AD")));

    assertThat(result.getTotalSemesters()).isEqualTo(3);
    assertThat(result.getSemesters().get(0).getStudyModuleKeys())
        .containsExactlyInAnyOrder("OO", "Math");
    assertThat(result.getSemesters().get(1).getStudyModuleKeys())
        .containsExactlyInAnyOrder("DB", "AD");
    assertThat(result.getSemesters().get(2).getStudyModuleKeys()).containsExactly("Thesis");
  }

  // --- calculateAndSaveSchedule ---

  @Test
  void calculateAndSaveSchedule_fetchesModulesForProgram_calculatesAndPersistsResult() {
    DegreeProgram program = new DegreeProgram(PROGRAM_ID, "SE", "", 6);
    when(studyModuleRepository.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of(module("OO"), module("DB", "OO")));

    testee.calculateAndSaveSchedule(program);

    ArgumentCaptor<Schedule> captor = ArgumentCaptor.forClass(Schedule.class);
    verify(scheduleRepository)
        .saveSchedule(org.mockito.ArgumentMatchers.eq(PROGRAM_ID), captor.capture());
    assertThat(captor.getValue().getTotalSemesters()).isEqualTo(2);
  }

  // --- findScheduleByDegreeProgramId ---

  @Test
  void findScheduleByDegreeProgramId_delegatesToRepository() {
    Schedule schedule = new Schedule(LocalDateTime.now(), List.of());
    when(scheduleRepository.findScheduleByDegreeProgramId(PROGRAM_ID))
        .thenReturn(Optional.of(schedule));

    assertThat(testee.findScheduleByDegreeProgramId(PROGRAM_ID)).contains(schedule);
  }

  @Test
  void findScheduleByDegreeProgramId_noScheduleExists_returnsEmpty() {
    when(scheduleRepository.findScheduleByDegreeProgramId(PROGRAM_ID)).thenReturn(Optional.empty());

    assertThat(testee.findScheduleByDegreeProgramId(PROGRAM_ID)).isEmpty();
  }

  // --- deleteSchedule ---

  @Test
  void deleteSchedule_delegatesToRepository() {
    testee.deleteSchedule(PROGRAM_ID);

    verify(scheduleRepository).deleteSchedule(PROGRAM_ID);
  }
}
