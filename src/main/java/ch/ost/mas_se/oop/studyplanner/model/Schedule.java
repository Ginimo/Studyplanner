package ch.ost.mas_se.oop.studyplanner.model;
import java.time.LocalDateTime;

public class Schedule {
    private LocalDateTime calculatedAt;
    private int totalSemesters;

    Schedule (LocalDateTime calculatedAt, int totalSemesters) {
        setTotalSemesters(totalSemesters);
        setCalculatedAt(calculatedAt);
    }

    // Setter
    void setTotalSemesters (int totalSemesters) {totalSemesters= this.totalSemesters;}
    void setCalculatedAt (LocalDateTime calculatedAt) {calculatedAt= this.calculatedAt;}

    // Getter
    int getTotalSemesters () {return totalSemesters;}
    LocalDateTime getCalculatedAt () {return calculatedAt;}


}
