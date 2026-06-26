package ch.ost.mas_se.oop.studyplanner.ui.views;

import ch.ost.mas_se.oop.studyplanner.model.Semester;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import java.util.List;
import java.util.stream.Collectors;

public record SemesterView(
    int semesterNumber,
    List<StudyModuleView> studyModules,
    int totalEcts,
    String joinedStudyModuleKeys) {

  public static SemesterView from(Semester semester) {
    List<StudyModuleView> modules =
        semester.getStudyModules().stream().map(StudyModuleView::from).collect(Collectors.toList());
    String joinedStudyModuleKeys =
        semester.getStudyModules().stream()
            .map(StudyModule::getStudyModuleKey)
            .collect(Collectors.joining(", "));
    return new SemesterView(
        semester.getSemesterNumber(), modules, semester.getTotalEcts(), joinedStudyModuleKeys);
  }
}
