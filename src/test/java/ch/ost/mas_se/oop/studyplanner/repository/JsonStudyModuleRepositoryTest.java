package ch.ost.mas_se.oop.studyplanner.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.serialization.StudyModuleMixin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonStudyModuleRepositoryTest {

  private static final String PROG_A = "prog-a";
  private static final String PROG_B = "prog-b";

  @TempDir Path tempDir;
  private JsonStudyModuleRepository testee;

  @BeforeEach
  void setUp() {
    ObjectMapper mapper =
        new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .addMixIn(StudyModule.class, StudyModuleMixin.class);
    testee = new JsonStudyModuleRepository(mapper, tempDir);
  }

  private static StudyModule module(String key, String progId) {
    return new StudyModule(key, progId, "Name-" + key, null, 4, List.of(), null, null);
  }

  @Test
  void findAllStudyModulesByDegreeProgramId_emptyStore_returnsEmptyList() {
    assertThat(testee.findAllStudyModulesByDegreeProgramId(PROG_A)).isEmpty();
  }

  @Test
  void saveStudyModule_newEntry_persistsAndCanBeRetrieved() {
    testee.saveStudyModule(module("OO", PROG_A));

    assertThat(testee.findStudyModuleByKey(PROG_A, "OO")).isPresent();
  }

  @Test
  void saveStudyModule_existingCompositeKey_replacesEntry() {
    testee.saveStudyModule(new StudyModule("OO", PROG_A, "Old", null, 3, List.of(), null, null));
    testee.saveStudyModule(new StudyModule("OO", PROG_A, "New", null, 6, List.of(), null, null));

    List<StudyModule> all = testee.findAllStudyModulesByDegreeProgramId(PROG_A);
    assertThat(all).hasSize(1);
    assertThat(all.getFirst().getName()).isEqualTo("New");
    assertThat(all.getFirst().getEcts()).isEqualTo(6);
  }

  @Test
  void saveStudyModule_sameKeyDifferentProgram_storedAsDistinctEntries() {
    testee.saveStudyModule(module("OO", PROG_A));
    testee.saveStudyModule(module("OO", PROG_B));

    assertThat(testee.findStudyModuleByKey(PROG_A, "OO")).isPresent();
    assertThat(testee.findStudyModuleByKey(PROG_B, "OO")).isPresent();
  }

  @Test
  void findStudyModuleByKey_unknownKey_returnsEmpty() {
    testee.saveStudyModule(module("OO", PROG_A));

    assertThat(testee.findStudyModuleByKey(PROG_A, "UNKNOWN")).isEmpty();
  }

  @Test
  void findStudyModuleByKey_unknownProgram_returnsEmpty() {
    testee.saveStudyModule(module("OO", PROG_A));

    assertThat(testee.findStudyModuleByKey(PROG_B, "OO")).isEmpty();
  }

  @Test
  void findAllStudyModulesByDegreeProgramId_returnsOnlyModulesOfGivenProgram() {
    testee.saveStudyModule(module("OO", PROG_A));
    testee.saveStudyModule(module("DB", PROG_A));
    testee.saveStudyModule(module("OO", PROG_B));

    List<StudyModule> result = testee.findAllStudyModulesByDegreeProgramId(PROG_A);

    assertThat(result).hasSize(2);
    assertThat(result).allMatch(m -> m.getDegreeProgramId().equals(PROG_A));
  }

  @Test
  void deleteAllStudyModulesByDegreeProgramId_removesOnlyModulesOfGivenProgram() {
    testee.saveStudyModule(module("OO", PROG_A));
    testee.saveStudyModule(module("DB", PROG_A));
    testee.saveStudyModule(module("OO", PROG_B));

    testee.deleteAllStudyModulesByDegreeProgramId(PROG_A);

    assertThat(testee.findAllStudyModulesByDegreeProgramId(PROG_A)).isEmpty();
    assertThat(testee.findAllStudyModulesByDegreeProgramId(PROG_B)).hasSize(1);
  }

  @Test
  void replaceAllStudyModulesForProgram_replacesOnlyTargetProgram() {
    testee.saveStudyModule(module("OO", PROG_A));
    testee.saveStudyModule(module("OO", PROG_B));
    StudyModule updated = new StudyModule("OO", PROG_A, "Updated", null, 8, List.of(), null, null);

    testee.replaceAllStudyModulesForProgram(PROG_A, List.of(updated));

    assertThat(testee.findStudyModuleByKey(PROG_A, "OO").orElseThrow().getName())
        .isEqualTo("Updated");
    assertThat(testee.findStudyModuleByKey(PROG_B, "OO")).isPresent();
  }

  @Test
  void replaceAllStudyModulesForProgram_withEmptyList_removesAllModulesForProgram() {
    testee.saveStudyModule(module("OO", PROG_A));
    testee.saveStudyModule(module("DB", PROG_A));

    testee.replaceAllStudyModulesForProgram(PROG_A, List.of());

    assertThat(testee.findAllStudyModulesByDegreeProgramId(PROG_A)).isEmpty();
  }
}
