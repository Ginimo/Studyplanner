package ch.ost.mas_se.oop.studyplanner.serialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class DegreeProgramMixin {

  @JsonCreator
  DegreeProgramMixin(
      @JsonProperty("degreeProgramId") String degreeProgramId,
      @JsonProperty("name") String name,
      @JsonProperty("description") String description,
      @JsonProperty("maxDurationInSemesters") int maxDurationInSemesters) {}
}
