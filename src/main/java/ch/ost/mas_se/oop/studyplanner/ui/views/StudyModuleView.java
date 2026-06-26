package ch.ost.mas_se.oop.studyplanner.ui.views;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import java.util.List;

public record StudyModuleView(
    String studyModuleKey,
    String name,
    String description,
    int ects,
    List<String> prerequisiteKeys) {

  public StudyModuleView {
    prerequisiteKeys = List.copyOf(prerequisiteKeys);
  }

  public static StudyModuleView from(StudyModule module) {
    return new StudyModuleView(
        module.getStudyModuleKey(),
        module.getName(),
        module.getDescription(),
        module.getEcts(),
        module.getPrerequisiteKeys());
  }
}
