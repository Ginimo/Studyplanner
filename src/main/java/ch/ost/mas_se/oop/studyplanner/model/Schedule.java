package ch.ost.mas_se.oop.studyplanner.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Schedule {
    private final LocalDateTime calculatedAt;
    private final List<Semester> semesters;

    public Schedule() {
        this.calculatedAt = LocalDateTime.now();
        this.semesters = new ArrayList<>();
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public List<Semester> getSemesters() {
        return Collections.unmodifiableList(semesters);
    }

    public void addSemester(Semester semester) {
        this.semesters.add(semester);
    }

    public int getTotalSemesters() {
        return semesters.size();
    }
}
