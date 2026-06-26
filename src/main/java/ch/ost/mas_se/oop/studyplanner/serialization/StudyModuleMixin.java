package ch.ost.mas_se.oop.studyplanner.serialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

public abstract class StudyModuleMixin {

  @JsonCreator
  StudyModuleMixin(
      @JsonProperty("studyModuleKey") String studyModuleKey,
      @JsonProperty("degreeProgramId") String degreeProgramId,
      @JsonProperty("name") String name,
      @JsonProperty("description") String description,
      @JsonProperty("ects") int ects,
      @JsonProperty("prerequisiteKeys") List<String> prerequisiteKeys,
      @JsonProperty("offeredFrom") LocalDate offeredFrom,
      @JsonProperty("offeredTo") LocalDate offeredTo) {}
}
