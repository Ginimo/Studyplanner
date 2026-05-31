package ch.ost.mas_se.oop.studyplanner.model;

public class Semester {
    private int semesterNumber;
    private int totalEcts;

    public Semester(int semesterNumber, int totalEcts){
        setSemesterNumber(semesterNumber);
        setTotalEcts(totalEcts);
    }

    // Setter
    public void setSemesterNumber (int semesterNumber) {this.semesterNumber=semesterNumber;}
    public void setTotalEcts (int totalEcts) {this.totalEcts=totalEcts;}

    // Getter
    public int getSemesterNumber () {return semesterNumber;}
    public int getTotalEcts () {return totalEcts;}

    // Printer
    public void printSemester(){
        IO.println("Semester configured as: ");
        IO.println("*******************" );
        IO.println("semesterNumber: " + semesterNumber);
        IO.println("totalEcts: " + totalEcts);
        IO.println("*******************");
    }

}
