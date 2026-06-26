package ch.ost.mas_se.oop.studyplanner.ui.views;

import ch.ost.mas_se.oop.studyplanner.model.Semester;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import java.util.List;
import java.util.stream.Collectors;

public class SemesterView {

  private final int semesterNumber;
  private final List<StudyModuleView> studyModules;
  private final int totalEcts;
  private final String studyModuleKeys;

  public static SemesterView from(Semester semester) {
    List<StudyModuleView> modules =
        semester.getStudyModules().stream().map(StudyModuleView::from).collect(Collectors.toList());
    String keys =
        semester.getStudyModules().stream()
            .map(StudyModule::getStudyModuleKey)
            .collect(Collectors.joining(", "));
    return new SemesterView(semester.getSemesterNumber(), modules, semester.getTotalEcts(), keys);
  }

  public SemesterView(
      final int semesterNumber,
      final List<StudyModuleView> studyModules,
      final int totalEcts,
      final String studyModuleKeys) {
    this.semesterNumber = semesterNumber;
    this.studyModules = studyModules;
    this.totalEcts = totalEcts;
    this.studyModuleKeys = studyModuleKeys;
  }

  public int getSemesterNumber() {
    return semesterNumber;
  }

  public List<StudyModuleView> getStudyModules() {
    return studyModules;
  }

  public int getTotalEcts() {
    return totalEcts;
  }

  public String getStudyModuleKeys() {
    return studyModuleKeys;
  }
}
