package ch.ost.mas_se.oop.studyplanner.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Semester {
  private final int semesterNumber;
  private final List<StudyModule> studyModules;

  /**
   * @param semesterNumber position of this semester in the study plan (1-based)
   */
  public Semester(int semesterNumber) {
    this.semesterNumber = semesterNumber;
    this.studyModules = new ArrayList<>();
  }

  @JsonCreator
  public Semester(
      @JsonProperty("semesterNumber") int semesterNumber,
      @JsonProperty("studyModules") List<StudyModule> studyModules) {
    this.semesterNumber = semesterNumber;
    this.studyModules = studyModules != null ? new ArrayList<>(studyModules) : new ArrayList<>();
  }

  public int getSemesterNumber() {
    return semesterNumber;
  }

  /** Returns an unmodifiable view of the modules assigned to this semester, in insertion order. */
  public List<StudyModule> getStudyModules() {
    return Collections.unmodifiableList(studyModules);
  }

  /** Appends {@code module} to this semester's module list. */
  public void addStudyModule(StudyModule module) {
    this.studyModules.add(module);
  }

  /** Returns the sum of ECTS credits for all modules in this semester. */
  public int getTotalEcts() {
    return studyModules.stream().mapToInt(StudyModule::getEcts).sum();
  }
}
