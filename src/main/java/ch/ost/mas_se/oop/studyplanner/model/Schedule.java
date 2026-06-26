package ch.ost.mas_se.oop.studyplanner.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Schedule {
  private final LocalDateTime calculatedAt;
  private final List<Semester> semesters;

  public Schedule(LocalDateTime calculatedAt, List<Semester> semesters) {
    this.calculatedAt = Objects.requireNonNull(calculatedAt);
    this.semesters = semesters != null ? new ArrayList<>(semesters) : new ArrayList<>();
  }

  public LocalDateTime getCalculatedAt() {
    return calculatedAt;
  }

  /** Returns an unmodifiable view of the semesters in this schedule, in chronological order. */
  public List<Semester> getSemesters() {
    return Collections.unmodifiableList(semesters);
  }

  /** Returns the total number of semesters in this schedule. */
  public int getTotalSemesters() {
    return semesters.size();
  }
}
