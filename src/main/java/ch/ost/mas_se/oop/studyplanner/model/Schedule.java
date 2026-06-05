package ch.ost.mas_se.oop.studyplanner.model;
import java.time.LocalDateTime;

public class Schedule {
    private LocalDateTime calculatedAt;
    private int totalSemesters;
    DegreeProgram degreeProgram;
    //Semester semester;

    public Schedule (
                LocalDateTime calculatedAt,
                int totalSemesters,
                DegreeProgram degreeProgram) {
        setTotalSemesters(totalSemesters);
        setCalculatedAt(calculatedAt);
        this.degreeProgram = degreeProgram;
    }

    // Setter
    public void setTotalSemesters (int totalSemesters) {this.totalSemesters=totalSemesters;}
    public void setCalculatedAt (LocalDateTime calculatedAt) {this.calculatedAt=calculatedAt;}

    // Getter
    public int getTotalSemesters () {return totalSemesters;}
    public LocalDateTime getCalculatedAt () {return calculatedAt;}


    // Printer
    public void printSchedule(){
        IO.println("Study Plan / Schedule configured as: ");
        IO.println("*******************" );
        IO.println("calculatedAt: " + calculatedAt);
        IO.println("totalSemesters: " + totalSemesters);
        IO.println("Degree Program: " + degreeProgram.getId());
        IO.println("*******************");
    }

}
