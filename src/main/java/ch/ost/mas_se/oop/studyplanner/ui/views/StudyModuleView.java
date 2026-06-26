package ch.ost.mas_se.oop.studyplanner.ui.views;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import java.util.List;

public class StudyModuleView {

  private final StudyModuleKey studyModuleKey;
  private final String name;
  private final String description;
  private final int ects;
  private final List<String> prerequisiteKeys;

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

  public StudyModuleKey getStudyModuleKey() {
    return studyModuleKey;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getEcts() {
    return ects;
  }

  public List<String> getPrerequisiteKeys() {
    return prerequisiteKeys;
  }
}
