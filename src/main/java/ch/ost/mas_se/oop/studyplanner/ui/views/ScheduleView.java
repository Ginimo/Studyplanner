package ch.ost.mas_se.oop.studyplanner.ui.views;

import java.time.LocalDateTime;

public class ScheduleView {

    private final LocalDateTime calculatedAt;
    private final int totalSemesters;

    public ScheduleView(final LocalDateTime calculatedAt, final int totalSemesters) {
        this.calculatedAt = calculatedAt;
        this.totalSemesters = totalSemesters;
    }

    public LocalDateTime getCalculatedAt() { return calculatedAt; }

    public int getTotalSemesters() { return totalSemesters; }
}
