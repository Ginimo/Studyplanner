package ch.ost.mas_se.oop.studyplanner.ui.views;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import java.time.LocalDate;

public record StudyModuleListView(
    StudyModuleKey studyModuleKey,
    String name,
    int ects,
    LocalDate offeredFrom,
    LocalDate offeredTo,
    boolean hasPrerequisites,
    int prerequisiteCount) {

  public static StudyModuleListView from(StudyModule module) {
    return new StudyModuleListView(
        new StudyModuleKey(module.getStudyModuleKey()),
        module.getName(),
        module.getEcts(),
        module.getOfferedFrom(),
        module.getOfferedTo(),
        !module.getPrerequisiteKeys().isEmpty(),
        module.getPrerequisiteKeys().size());
  }
}
