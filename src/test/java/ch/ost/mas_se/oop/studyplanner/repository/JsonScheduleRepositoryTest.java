package ch.ost.mas_se.oop.studyplanner.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.model.Semester;
import ch.ost.mas_se.oop.studyplanner.serialization.ScheduleMixin;
import ch.ost.mas_se.oop.studyplanner.serialization.SemesterMixin;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonScheduleRepositoryTest {

  @TempDir Path tempDir;
  private JsonScheduleRepository testee;

  @BeforeEach
  void setUp() {
    ObjectMapper mapper =
        new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .addMixIn(Schedule.class, ScheduleMixin.class)
            .addMixIn(Semester.class, SemesterMixin.class);
    testee = new JsonScheduleRepository(mapper, tempDir);
  }

  private static Schedule scheduleWithKeys(String... keys) {
    return new Schedule(
        LocalDateTime.of(2026, 1, 1, 12, 0), List.of(new Semester(1, List.of(keys))));
  }

  @Test
  void findScheduleByDegreeProgramId_emptyStore_returnsEmpty() {
    assertThat(testee.findScheduleByDegreeProgramId("prog-1")).isEmpty();
  }

  @Test
  void saveSchedule_newEntry_canBeRetrieved() {
    testee.saveSchedule("prog-1", scheduleWithKeys("OO", "Math"));

    Schedule found = testee.findScheduleByDegreeProgramId("prog-1").orElseThrow();
    assertThat(found.getTotalSemesters()).isEqualTo(1);
    assertThat(found.semesters().get(0).getStudyModuleKeys()).containsExactly("OO", "Math");
  }

  @Test
  void saveSchedule_existingKey_overwrites() {
    testee.saveSchedule("prog-1", scheduleWithKeys("OLD"));
    testee.saveSchedule("prog-1", scheduleWithKeys("NEW"));

    Schedule found = testee.findScheduleByDegreeProgramId("prog-1").orElseThrow();
    assertThat(found.semesters().get(0).getStudyModuleKeys()).containsExactly("NEW");
  }

  @Test
  void saveSchedule_differentProgramIds_storedIndependently() {
    testee.saveSchedule("prog-1", scheduleWithKeys("A"));
    testee.saveSchedule("prog-2", scheduleWithKeys("B"));

    assertThat(
            testee
                .findScheduleByDegreeProgramId("prog-1")
                .orElseThrow()
                .semesters()
                .get(0)
                .getStudyModuleKeys())
        .containsExactly("A");
    assertThat(
            testee
                .findScheduleByDegreeProgramId("prog-2")
                .orElseThrow()
                .semesters()
                .get(0)
                .getStudyModuleKeys())
        .containsExactly("B");
  }

  @Test
  void findScheduleByDegreeProgramId_unknownKey_returnsEmpty() {
    testee.saveSchedule("prog-1", scheduleWithKeys("OO"));

    assertThat(testee.findScheduleByDegreeProgramId("other")).isEmpty();
  }

  @Test
  void deleteSchedule_existing_removesEntry() {
    testee.saveSchedule("prog-1", scheduleWithKeys("OO"));

    testee.deleteSchedule("prog-1");

    assertThat(testee.findScheduleByDegreeProgramId("prog-1")).isEmpty();
  }

  @Test
  void deleteSchedule_unknown_isNoOpAndLeavesOtherEntriesIntact() {
    testee.saveSchedule("prog-1", scheduleWithKeys("OO"));

    testee.deleteSchedule("other");

    assertThat(testee.findScheduleByDegreeProgramId("prog-1")).isPresent();
  }
}
