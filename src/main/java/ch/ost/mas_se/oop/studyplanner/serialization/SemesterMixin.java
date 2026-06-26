package ch.ost.mas_se.oop.studyplanner.serialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public abstract class SemesterMixin {

  @JsonCreator
  SemesterMixin(
      @JsonProperty("semesterNumber") int semesterNumber,
      @JsonProperty("studyModuleKeys") List<String> studyModuleKeys) {}
}
