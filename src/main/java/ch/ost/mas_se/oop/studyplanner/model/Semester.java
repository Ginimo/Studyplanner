package ch.ost.mas_se.oop.studyplanner.model;

import java.util.List;

public record Semester(int semesterNumber, List<String> studyModuleKeys) {
  public Semester {
    if (semesterNumber < 1) {
      throw new IllegalArgumentException(
          "semesterNumber must be at least 1, got: " + semesterNumber);
    }

    studyModuleKeys = studyModuleKeys != null ? List.copyOf(studyModuleKeys) : List.of();
  }
}
