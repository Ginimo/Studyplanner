package ch.ost.mas_se.oop.studyplanner.ui.views;

import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import java.time.LocalDateTime;

public class ScheduleView {

  private final LocalDateTime calculatedAt;
  private final int totalSemesters;

  public static ScheduleView toView(Schedule schedule) {
    return new ScheduleView(schedule.getCalculatedAt(), schedule.getTotalSemesters());
  }

  public ScheduleView(final LocalDateTime calculatedAt, final int totalSemesters) {
    this.calculatedAt = calculatedAt;
    this.totalSemesters = totalSemesters;
  }

  public LocalDateTime getCalculatedAt() {
    return calculatedAt;
  }

  public int getTotalSemesters() {
    return totalSemesters;
  }
}
