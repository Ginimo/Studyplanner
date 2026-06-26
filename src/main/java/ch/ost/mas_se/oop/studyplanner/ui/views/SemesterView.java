package ch.ost.mas_se.oop.studyplanner.ui.views;

import java.util.List;

public class SemesterView {

  private final int semesterNumber;
  private final List<StudyModuleView> studyModules;
  private final int totalEcts;
  private final String studyModuleKeys;

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
