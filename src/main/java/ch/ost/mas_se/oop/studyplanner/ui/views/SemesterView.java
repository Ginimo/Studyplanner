package ch.ost.mas_se.oop.studyplanner.ui.views;

import ch.ost.mas_se.oop.studyplanner.model.Semester;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record SemesterView(
    int semesterNumber,
    List<StudyModuleView> studyModules,
    int totalEcts,
    String joinedStudyModuleKeys) {

  public static SemesterView from(Semester semester, Map<String, StudyModule> moduleMap) {
    List<StudyModule> resolved =
        semester.studyModuleKeys().stream().map(moduleMap::get).filter(Objects::nonNull).toList();

    return new SemesterView(
        semester.semesterNumber(),
        resolved.stream().map(StudyModuleView::from).toList(),
        resolved.stream().mapToInt(StudyModule::getEcts).sum(),
        String.join(", ", semester.studyModuleKeys()));
  }
}
