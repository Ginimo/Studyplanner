package ch.ost.mas_se.oop.studyplanner.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Schedule {
    private final LocalDateTime calculatedAt;
    private final List<Semester> semesters;

    /**
     * Creates an empty schedule and captures the current timestamp as the calculation time.
     */
    public Schedule() {
        this.calculatedAt = LocalDateTime.now();
        this.semesters = new ArrayList<>();
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    /** Returns an unmodifiable view of the semesters in this schedule, in chronological order. */
    public List<Semester> getSemesters() {
        return Collections.unmodifiableList(semesters);
    }

    /** Appends {@code semester} to this schedule. */
    public void addSemester(Semester semester) {
        this.semesters.add(semester);
    }

    /** Returns the total number of semesters in this schedule. */
    public int getTotalSemesters() {
        return semesters.size();
    }
}
