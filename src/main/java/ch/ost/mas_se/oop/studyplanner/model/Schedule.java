package ch.ost.mas_se.oop.studyplanner.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Schedule {
  private final LocalDateTime calculatedAt;
  private final List<Semester> semesters;

  /** Creates an empty schedule and captures the current timestamp as the calculation time. */
  public Schedule() {
    this.calculatedAt = LocalDateTime.now();
    this.semesters = new ArrayList<>();
  }

  public Schedule(LocalDateTime calculatedAt, List<Semester> semesters) {
    this.calculatedAt = calculatedAt;
    this.semesters = semesters != null ? new ArrayList<>(semesters) : new ArrayList<>();
  }

  public LocalDateTime getCalculatedAt() {
    return calculatedAt;
  }

  /** Returns an unmodifiable view of the semesters in this schedule, in chronological order. */
  public List<Semester> getSemesters() {
    return Collections.unmodifiableList(semesters);
  }

  /**
   * Appends {@code semester} to this schedule. Semester numbers must be consecutive starting at 1:
   * the first added must be semester 1, the second semester 2, and so on.
   */
  public void addSemester(Semester semester) {
    Objects.requireNonNull(semester);
    int expected = semesters.size() + 1;
    if (semester.getSemesterNumber() != expected) {
      throw new IllegalArgumentException(
          "Expected semester " + expected + " but got " + semester.getSemesterNumber());
    }
    this.semesters.add(semester);
  }

  /** Returns the total number of semesters in this schedule. */
  public int getTotalSemesters() {
    return semesters.size();
  }
}
