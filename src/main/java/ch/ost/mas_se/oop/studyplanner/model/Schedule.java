package ch.ost.mas_se.oop.studyplanner.model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private LocalDateTime calculatedAt;
    private int totalSemesters;
    private final DegreeProgram degreeProgram;
    private final List<Semester> semesters = new ArrayList<>();;

    public Schedule (
                int totalSemesters,
                DegreeProgram degreeProgram) {
        setTotalSemesters(totalSemesters);
        this.degreeProgram = degreeProgram;
    }

    // Setter
    public void setTotalSemesters (int totalSemesters) {this.totalSemesters=totalSemesters;}
    public void setCalculatedAt (LocalDateTime calculatedAt) {this.calculatedAt=calculatedAt;}

    // Getter
    public int getTotalSemesters () {return totalSemesters;}
    public LocalDateTime getCalculatedAt () {return calculatedAt;}
    public String getDegreeProgramID(){return degreeProgram.getId();}
    public List<Semester> getSemesters() { return semesters; }

    // Methoden
    public void addSemester(Semester semester) {
        semesters.add(semester); }

    // Printer
    public void printSchedule(){
        IO.println("Study Plan / Schedule configured as: ");
        IO.println("*******************" );
        IO.println("calculatedAt: " + calculatedAt);
        IO.println("totalSemesters: " + totalSemesters);
        IO.println("Degree Program: " + degreeProgram.getId());
        IO.println("Planned Semesters in this Study Plan: ");
        IO.println("*******************");
        for (Semester semester : semesters){
            semester.printSemester();;
        }
        IO.println("*******************");
    }

}
