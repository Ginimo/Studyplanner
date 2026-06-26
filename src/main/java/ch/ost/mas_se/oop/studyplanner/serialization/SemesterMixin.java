package ch.ost.mas_se.oop.studyplanner.serialization;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public abstract class SemesterMixin {

  @JsonCreator
  SemesterMixin(
      @JsonProperty("semesterNumber") int semesterNumber,
      @JsonProperty("studyModules") List<StudyModule> studyModules) {}
}
