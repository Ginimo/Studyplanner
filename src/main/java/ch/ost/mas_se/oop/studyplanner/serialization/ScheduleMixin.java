package ch.ost.mas_se.oop.studyplanner.serialization;

import ch.ost.mas_se.oop.studyplanner.model.Semester;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public abstract class ScheduleMixin {

  @JsonCreator
  ScheduleMixin(
      @JsonProperty("calculatedAt") LocalDateTime calculatedAt,
      @JsonProperty("semesters") List<Semester> semesters) {}
}
