package ch.ost.mas_se.oop.studyplanner.ui.views;

import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import java.time.LocalDateTime;

public record ScheduleView(LocalDateTime calculatedAt, int totalSemesters) {

  public static ScheduleView from(Schedule schedule) {
    return new ScheduleView(schedule.calculatedAt(), schedule.getTotalSemesters());
  }
}
