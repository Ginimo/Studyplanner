package ch.ost.mas_se.oop.studyplanner.ui.views;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import java.util.List;

public record StudyModuleView(
    StudyModuleKey studyModuleKey,
    String name,
    String description,
    int ects,
    List<String> prerequisiteKeys) {

  public static StudyModuleView from(StudyModule module) {
    return new StudyModuleView(
        new StudyModuleKey(module.getStudyModuleKey()),
        module.getName(),
        module.getDescription(),
        module.getEcts(),
        module.getPrerequisiteKeys());
  }

  public StudyModuleView(
      final StudyModuleKey studyModuleKey,
      final String name,
      final String description,
      final int ects,
      final List<String> prerequisiteKeys) {
    this.studyModuleKey = studyModuleKey;
    this.name = name;
    this.description = description;
    this.ects = ects;
    this.prerequisiteKeys = List.copyOf(prerequisiteKeys);
  }
}
