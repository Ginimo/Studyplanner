package ch.ost.mas_se.oop.studyplanner.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Semester {
  private final int semesterNumber;
  private final List<String> studyModuleKeys;

  /**
   * @param semesterNumber position of this semester in the study plan (1-based)
   */
  public Semester(int semesterNumber) {
    if (semesterNumber < 1) {
      throw new IllegalArgumentException(
          "semesterNumber must be at least 1, got: " + semesterNumber);
    }
    this.semesterNumber = semesterNumber;
    this.studyModuleKeys = new ArrayList<>();
  }

  public Semester(int semesterNumber, List<String> studyModuleKeys) {
    if (semesterNumber < 1) {
      throw new IllegalArgumentException(
          "semesterNumber must be at least 1, got: " + semesterNumber);
    }
    this.semesterNumber = semesterNumber;
    this.studyModuleKeys =
        studyModuleKeys != null ? new ArrayList<>(studyModuleKeys) : new ArrayList<>();
  }

  public int getSemesterNumber() {
    return semesterNumber;
  }

  /** Returns an unmodifiable view of module keys assigned to this semester, in placement order. */
  public List<String> getStudyModuleKeys() {
    return Collections.unmodifiableList(studyModuleKeys);
  }

  /** Appends {@code key} to this semester's module key list. */
  public void addStudyModuleKey(String key) {
    this.studyModuleKeys.add(key);
  }
}
