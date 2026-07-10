package ch.ost.mas_se.oop.studyplanner.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Schedule(
  LocalDateTime calculatedAt,
  List<Semester> semesters
) {

  public Schedule {
    calculatedAt = Objects.requireNonNull(calculatedAt);
    semesters = semesters != null ? new ArrayList<>(semesters) : new ArrayList<>();
  }

  /** Returns the total number of semesters in this schedule. */
  public int getTotalSemesters() {
    return semesters.size();
  }
}
