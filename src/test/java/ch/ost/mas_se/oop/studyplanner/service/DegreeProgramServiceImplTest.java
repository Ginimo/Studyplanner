package ch.ost.mas_se.oop.studyplanner.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.ost.mas_se.oop.studyplanner.exception.DuplicateDegreeProgramNameException;
import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.repository.DegreeProgramRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DegreeProgramServiceImplTest {

  private static final String PROGRAM_ID = "prog-1";

  @Mock private DegreeProgramRepository degreeProgramRepository;
  @Mock private StudyModuleService studyModuleService;

  @InjectMocks private DegreeProgramServiceImpl testee;

  @Test
  void createDegreeProgram_duplicateName_throwsDuplicateDegreeProgramNameException() {
    when(degreeProgramRepository.findAllDegreePrograms())
        .thenReturn(List.of(new DegreeProgram(PROGRAM_ID, "Software Engineering", "", 6)));

    assertThatThrownBy(() -> testee.createDegreeProgram("Software Engineering", "desc", 6))
        .isInstanceOf(DuplicateDegreeProgramNameException.class)
        .hasMessageContaining("Software Engineering");
  }

  @Test
  void createDegreeProgram_generatesIdAndSavesToRepository() {
    DegreeProgram result = testee.createDegreeProgram("Software Engineering", "desc", 6);

    assertThat(result.getName()).isEqualTo("Software Engineering");
    assertThat(result.getDescription()).isEqualTo("desc");
    assertThat(result.getMaxDurationInSemesters()).isEqualTo(6);
    assertThat(result.getDegreeProgramId()).isNotBlank();
    verify(degreeProgramRepository).saveDegreeProgram(result);
  }

  @Test
  void createDegreeProgram_eachCallGeneratesDistinctId() {
    DegreeProgram first = testee.createDegreeProgram("A", "", 4);
    DegreeProgram second = testee.createDegreeProgram("B", "", 4);

    assertThat(first.getDegreeProgramId()).isNotEqualTo(second.getDegreeProgramId());
  }

  @Test
  void findDegreeProgramById_delegatesToRepository() {
    DegreeProgram program = new DegreeProgram(PROGRAM_ID, "SE", "", 6);
    when(degreeProgramRepository.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(program));

    assertThat(testee.findDegreeProgramById(PROGRAM_ID)).contains(program);
  }

  @Test
  void findDegreeProgramById_unknownId_returnsEmpty() {
    when(degreeProgramRepository.findDegreeProgramById(PROGRAM_ID)).thenReturn(Optional.empty());

    assertThat(testee.findDegreeProgramById(PROGRAM_ID)).isEmpty();
  }

  @Test
  void findAllDegreePrograms_delegatesToRepository() {
    DegreeProgram program = new DegreeProgram(PROGRAM_ID, "SE", "", 6);
    when(degreeProgramRepository.findAllDegreePrograms()).thenReturn(List.of(program));

    assertThat(testee.findAllDegreePrograms()).containsExactly(program);
  }

  @Test
  void updateDegreeProgram_savesNewInstanceWithSameIdAndUpdatedFields() {
    testee.updateDegreeProgram(PROGRAM_ID, "New Name", "New Desc", 8);

    ArgumentCaptor<DegreeProgram> captor = ArgumentCaptor.forClass(DegreeProgram.class);
    verify(degreeProgramRepository).saveDegreeProgram(captor.capture());
    DegreeProgram saved = captor.getValue();
    assertThat(saved.getDegreeProgramId()).isEqualTo(PROGRAM_ID);
    assertThat(saved.getName()).isEqualTo("New Name");
    assertThat(saved.getDescription()).isEqualTo("New Desc");
    assertThat(saved.getMaxDurationInSemesters()).isEqualTo(8);
  }

  @Test
  void deleteDegreeProgram_deletesAllModulesCascadingThenDeletesProgram() {
    testee.deleteDegreeProgram(PROGRAM_ID);

    verify(studyModuleService).deleteAllStudyModulesByDegreeProgramId(PROGRAM_ID);
    verify(degreeProgramRepository).deleteDegreeProgram(PROGRAM_ID);
  }
}
