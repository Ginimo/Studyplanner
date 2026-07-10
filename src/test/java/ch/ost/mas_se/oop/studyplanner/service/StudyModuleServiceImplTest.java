package ch.ost.mas_se.oop.studyplanner.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.ost.mas_se.oop.studyplanner.exception.DuplicateStudyModuleKeyException;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.repository.StudyModuleRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyModuleServiceImplTest {

  private static final String PROGRAM_ID = "prog-1";
  private static final String KEY_A = "MOD-A";
  private static final String KEY_B = "MOD-B";
  private static final String KEY_C = "MOD-C";

  @Mock private StudyModuleRepository studyModuleRepository;
  @Mock private ScheduleService scheduleService;

  @Captor private ArgumentCaptor<List<StudyModule>> moduleListCaptor;

  @InjectMocks private StudyModuleServiceImpl testee;

  private static StudyModule module(String key, List<String> prereqs) {
    return new StudyModule(key, PROGRAM_ID, "Name-" + key, null, 4, prereqs, null, null);
  }

  @Test
  void createStudyModule_duplicateKey_throwsDuplicateStudyModuleKeyException() {
    when(studyModuleRepository.findStudyModuleByKey(PROGRAM_ID, KEY_A))
        .thenReturn(Optional.of(module(KEY_A, List.of())));

    assertThatThrownBy(
            () ->
                testee.createStudyModule(
                    KEY_A, PROGRAM_ID, "Name", "desc", 4, List.of(), null, null))
        .isInstanceOf(DuplicateStudyModuleKeyException.class)
        .hasMessageContaining(KEY_A);
  }

  @Test
  void createStudyModule_savesModuleToRepositoryAndInvalidatesSchedule() {
    when(studyModuleRepository.findStudyModuleByKey(PROGRAM_ID, KEY_A))
        .thenReturn(Optional.empty());
    testee.createStudyModule(KEY_A, PROGRAM_ID, "Name", "desc", 4, List.of(), null, null);

    ArgumentCaptor<StudyModule> captor = ArgumentCaptor.forClass(StudyModule.class);
    verify(studyModuleRepository).saveStudyModule(captor.capture());
    StudyModule saved = captor.getValue();
    assertThat(saved.getStudyModuleKey()).isEqualTo(KEY_A);
    assertThat(saved.getDegreeProgramId()).isEqualTo(PROGRAM_ID);
    assertThat(saved.getName()).isEqualTo("Name");
    verify(scheduleService).deleteSchedule(PROGRAM_ID);
  }

  @Test
  void findStudyModuleByKey_delegatesToRepository() {
    StudyModule m = module(KEY_A, List.of());
    when(studyModuleRepository.findStudyModuleByKey(PROGRAM_ID, KEY_A)).thenReturn(Optional.of(m));

    assertThat(testee.findStudyModuleByKey(PROGRAM_ID, KEY_A)).contains(m);
  }

  @Test
  void findStudyModuleByKey_unknownKey_returnsEmpty() {
    when(studyModuleRepository.findStudyModuleByKey(PROGRAM_ID, KEY_A))
        .thenReturn(Optional.empty());

    assertThat(testee.findStudyModuleByKey(PROGRAM_ID, KEY_A)).isEmpty();
  }

  @Test
  void findAllStudyModulesByDegreeProgramId_delegatesToRepository() {
    StudyModule m = module(KEY_A, List.of());
    when(studyModuleRepository.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of(m));

    assertThat(testee.findAllStudyModulesByDegreeProgramId(PROGRAM_ID)).containsExactly(m);
  }

  @Test
  void findStudyModulesByKeys_returnsOnlyModulesMatchingGivenKeys() {
    StudyModule a = module(KEY_A, List.of());
    StudyModule b = module(KEY_B, List.of());
    when(studyModuleRepository.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of(a, b));

    List<StudyModule> result = testee.findStudyModulesByKeys(PROGRAM_ID, List.of(KEY_A));

    assertThat(result).containsExactly(a);
  }

  @Test
  void findStudyModulesByKeys_unknownKeysAreSkippedSilently() {
    StudyModule a = module(KEY_A, List.of());
    when(studyModuleRepository.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of(a));

    assertThat(testee.findStudyModulesByKeys(PROGRAM_ID, List.of("UNKNOWN"))).isEmpty();
  }

  @Test
  void updateStudyModule_savesUpdatedModuleAndInvalidatesSchedule() {
    testee.updateStudyModule(KEY_A, PROGRAM_ID, "Updated", "desc", 6, List.of(), null, null);

    ArgumentCaptor<StudyModule> captor = ArgumentCaptor.forClass(StudyModule.class);
    verify(studyModuleRepository).saveStudyModule(captor.capture());
    assertThat(captor.getValue().getName()).isEqualTo("Updated");
    assertThat(captor.getValue().getEcts()).isEqualTo(6);
    verify(scheduleService).deleteSchedule(PROGRAM_ID);
  }

  @Test
  void deleteStudyModule_removesTargetModuleFromStorageAndInvalidatesSchedule() {
    when(studyModuleRepository.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of(module(KEY_A, List.of()), module(KEY_B, List.of())));

    testee.deleteStudyModule(PROGRAM_ID, KEY_A);

    verify(studyModuleRepository)
        .replaceAllStudyModulesForProgram(
            org.mockito.ArgumentMatchers.eq(PROGRAM_ID), moduleListCaptor.capture());
    assertThat(moduleListCaptor.getValue()).hasSize(1);
    assertThat(moduleListCaptor.getValue().get(0).getStudyModuleKey()).isEqualTo(KEY_B);
    verify(scheduleService).deleteSchedule(PROGRAM_ID);
  }

  @Test
  void deleteStudyModule_removesDanglingPrerequisiteKeyFromSiblingModules() {
    StudyModule dependent = module(KEY_B, List.of(KEY_A));
    StudyModule unrelated = module(KEY_C, List.of());
    when(studyModuleRepository.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of(module(KEY_A, List.of()), dependent, unrelated));

    testee.deleteStudyModule(PROGRAM_ID, KEY_A);

    verify(studyModuleRepository)
        .replaceAllStudyModulesForProgram(
            org.mockito.ArgumentMatchers.eq(PROGRAM_ID), moduleListCaptor.capture());
    List<StudyModule> saved = moduleListCaptor.getValue();
    assertThat(saved).hasSize(2);
    StudyModule updatedDependent =
        saved.stream().filter(m -> m.getStudyModuleKey().equals(KEY_B)).findFirst().orElseThrow();
    assertThat(updatedDependent.getPrerequisiteKeys()).doesNotContain(KEY_A);
    StudyModule keptUnrelated =
        saved.stream().filter(m -> m.getStudyModuleKey().equals(KEY_C)).findFirst().orElseThrow();
    assertThat(keptUnrelated.getPrerequisiteKeys()).isEmpty();
  }

  @Test
  void deleteAllStudyModulesByDegreeProgramId_delegatesToRepositoryAndInvalidatesSchedule() {
    testee.deleteAllStudyModulesByDegreeProgramId(PROGRAM_ID);

    verify(studyModuleRepository).deleteAllStudyModulesByDegreeProgramId(PROGRAM_ID);
    verify(scheduleService).deleteSchedule(PROGRAM_ID);
  }

  // --- findFilteredStudyModules ---

  @Test
  void findFilteredStudyModules_blankNameFilter_returnsAllModules() {
    StudyModule a = module(KEY_A, List.of());
    StudyModule b = module(KEY_B, List.of());
    when(studyModuleRepository.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of(a, b));

    List<StudyModule> result = testee.findFilteredStudyModules(PROGRAM_ID, "", null, "NAME", "ASC");

    assertThat(result).containsExactlyInAnyOrder(a, b);
  }

  @Test
  void findFilteredStudyModules_nameFilter_returnsCaseInsensitiveMatches() {
    StudyModule a = new StudyModule(KEY_A, PROGRAM_ID, "Algebra", null, 4, List.of(), null, null);
    StudyModule b = new StudyModule(KEY_B, PROGRAM_ID, "Biology", null, 4, List.of(), null, null);
    when(studyModuleRepository.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of(a, b));

    List<StudyModule> result =
        testee.findFilteredStudyModules(PROGRAM_ID, "alg", null, "NAME", "ASC");

    assertThat(result).containsExactly(a);
  }

  @Test
  void findFilteredStudyModules_offeredAtFilter_excludesModulesOutsideDateRange() {
    LocalDate from = LocalDate.of(2025, 1, 1);
    LocalDate to = LocalDate.of(2025, 12, 31);
    StudyModule inRange =
        new StudyModule(KEY_A, PROGRAM_ID, "Name-A", null, 4, List.of(), from, to);
    StudyModule outOfRange =
        new StudyModule(KEY_B, PROGRAM_ID, "Name-B", null, 4, List.of(), from, to);
    when(studyModuleRepository.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of(inRange, outOfRange));

    List<StudyModule> result =
        testee.findFilteredStudyModules(PROGRAM_ID, "", LocalDate.of(2025, 6, 15), "NAME", "ASC");

    assertThat(result).containsExactly(inRange, outOfRange);

    List<StudyModule> noMatch =
        testee.findFilteredStudyModules(PROGRAM_ID, "", LocalDate.of(2026, 6, 15), "NAME", "ASC");

    assertThat(noMatch).isEmpty();
  }

  @Test
  void findFilteredStudyModules_offeredAtFilter_excludesModulesWithMissingDateRange() {
    LocalDate from = LocalDate.of(2025, 1, 1);
    LocalDate to = LocalDate.of(2025, 12, 31);
    StudyModule completeRange =
        new StudyModule(KEY_A, PROGRAM_ID, "Name-A", null, 4, List.of(), from, to);
    StudyModule missingFrom =
        new StudyModule(KEY_B, PROGRAM_ID, "Name-B", null, 4, List.of(), null, to);
    StudyModule missingTo =
        new StudyModule(KEY_C, PROGRAM_ID, "Name-C", null, 4, List.of(), from, null);
    when(studyModuleRepository.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of(completeRange, missingFrom, missingTo));

    List<StudyModule> result =
        testee.findFilteredStudyModules(PROGRAM_ID, "", LocalDate.of(2025, 6, 15), "NAME", "ASC");

    assertThat(result).containsExactly(completeRange);
  }

  @Test
  void findFilteredStudyModules_sortByEctsDesc_returnsModulesInDescendingOrder() {
    StudyModule low = new StudyModule(KEY_A, PROGRAM_ID, "Name-A", null, 2, List.of(), null, null);
    StudyModule mid = new StudyModule(KEY_B, PROGRAM_ID, "Name-B", null, 4, List.of(), null, null);
    StudyModule high = new StudyModule(KEY_C, PROGRAM_ID, "Name-C", null, 6, List.of(), null, null);
    when(studyModuleRepository.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of(low, mid, high));

    List<StudyModule> result =
        testee.findFilteredStudyModules(PROGRAM_ID, "", null, "ECTS", "DESC");

    assertThat(result).containsExactly(high, mid, low);
  }

  @Test
  void findFilteredStudyModules_sortByOfferedFrom_keepsMissingDatesLast() {
    LocalDate earlyDate = LocalDate.of(2025, 1, 1);
    LocalDate lateDate = LocalDate.of(2026, 1, 1);
    StudyModule early =
        new StudyModule(KEY_A, PROGRAM_ID, "Name-A", null, 4, List.of(), earlyDate, null);
    StudyModule missing =
        new StudyModule(KEY_B, PROGRAM_ID, "Name-B", null, 4, List.of(), null, null);
    StudyModule late =
        new StudyModule(KEY_C, PROGRAM_ID, "Name-C", null, 4, List.of(), lateDate, null);
    when(studyModuleRepository.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of(missing, late, early));

    List<StudyModule> ascending =
        testee.findFilteredStudyModules(PROGRAM_ID, "", null, "OFFERED_FROM", "ASC");
    List<StudyModule> descending =
        testee.findFilteredStudyModules(PROGRAM_ID, "", null, "OFFERED_FROM", "DESC");

    assertThat(ascending).containsExactly(early, late, missing);
    assertThat(descending).containsExactly(late, early, missing);
  }

  @Test
  void findFilteredStudyModules_sortByOfferedTo_keepsMissingDatesLast() {
    LocalDate earlyDate = LocalDate.of(2025, 1, 1);
    LocalDate lateDate = LocalDate.of(2026, 1, 1);
    StudyModule early =
        new StudyModule(KEY_A, PROGRAM_ID, "Name-A", null, 4, List.of(), null, earlyDate);
    StudyModule missing =
        new StudyModule(KEY_B, PROGRAM_ID, "Name-B", null, 4, List.of(), null, null);
    StudyModule late =
        new StudyModule(KEY_C, PROGRAM_ID, "Name-C", null, 4, List.of(), null, lateDate);
    when(studyModuleRepository.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of(missing, late, early));

    List<StudyModule> ascending =
        testee.findFilteredStudyModules(PROGRAM_ID, "", null, "OFFERED_TO", "ASC");
    List<StudyModule> descending =
        testee.findFilteredStudyModules(PROGRAM_ID, "", null, "OFFERED_TO", "DESC");

    assertThat(ascending).containsExactly(early, late, missing);
    assertThat(descending).containsExactly(late, early, missing);
  }

  // --- findModuleMapByDegreeProgramId ---

  @Test
  void findModuleMapByDegreeProgramId_returnsAllModulesIndexedByKey() {
    StudyModule a = module(KEY_A, List.of());
    StudyModule b = module(KEY_B, List.of());
    when(studyModuleRepository.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of(a, b));

    Map<String, StudyModule> result = testee.findModuleMapByDegreeProgramId(PROGRAM_ID);

    assertThat(result).hasSize(2).containsEntry(KEY_A, a).containsEntry(KEY_B, b);
  }

  @Test
  void findModuleMapByDegreeProgramId_emptyProgram_returnsEmptyMap() {
    when(studyModuleRepository.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of());

    assertThat(testee.findModuleMapByDegreeProgramId(PROGRAM_ID)).isEmpty();
  }
}
