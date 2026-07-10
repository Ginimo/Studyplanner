package ch.ost.mas_se.oop.studyplanner.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.serialization.DegreeProgramMixin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonDegreeProgramRepositoryTest {

  @TempDir Path tempDir;
  private JsonDegreeProgramRepository testee;

  @BeforeEach
  void setUp() {
    ObjectMapper mapper =
        new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .addMixIn(DegreeProgram.class, DegreeProgramMixin.class);
    testee = new JsonDegreeProgramRepository(mapper, tempDir);
  }

  @Test
  void findAllDegreePrograms_emptyStore_returnsEmptyList() {
    assertThat(testee.findAllDegreePrograms()).isEmpty();
  }

  @Test
  void saveDegreeProgram_newEntry_persistsAndCanBeRetrieved() {
    DegreeProgram program = new DegreeProgram("id-1", "Software Engineering", "desc", 6);

    testee.saveDegreeProgram(program);

    assertThat(testee.findDegreeProgramById("id-1")).contains(program);
  }

  @Test
  void saveDegreeProgram_existingId_replacesEntry() {
    testee.saveDegreeProgram(new DegreeProgram("id-1", "Old Name", "", 6));
    testee.saveDegreeProgram(new DegreeProgram("id-1", "New Name", "", 8));

    List<DegreeProgram> all = testee.findAllDegreePrograms();
    assertThat(all).hasSize(1);
    assertThat(all.get(0).getName()).isEqualTo("New Name");
    assertThat(all.get(0).getMaxDurationInSemesters()).isEqualTo(8);
  }

  @Test
  void findDegreeProgramById_unknownId_returnsEmpty() {
    assertThat(testee.findDegreeProgramById("unknown")).isEmpty();
  }

  @Test
  void findAllDegreePrograms_multipleEntries_returnsAll() {
    testee.saveDegreeProgram(new DegreeProgram("id-1", "A", "", 4));
    testee.saveDegreeProgram(new DegreeProgram("id-2", "B", "", 6));

    assertThat(testee.findAllDegreePrograms()).hasSize(2);
  }

  @Test
  void deleteDegreeProgram_existing_removesEntry() {
    testee.saveDegreeProgram(new DegreeProgram("id-1", "A", "", 4));

    testee.deleteDegreeProgram("id-1");

    assertThat(testee.findDegreeProgramById("id-1")).isEmpty();
  }

  @Test
  void deleteDegreeProgram_unknown_isNoOpAndLeavesOtherEntriesIntact() {
    testee.saveDegreeProgram(new DegreeProgram("id-1", "A", "", 4));

    testee.deleteDegreeProgram("non-existent");

    assertThat(testee.findAllDegreePrograms()).hasSize(1);
  }
}
