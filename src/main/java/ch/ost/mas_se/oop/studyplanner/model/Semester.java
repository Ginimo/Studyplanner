package ch.ost.mas_se.oop.studyplanner.model;

public class Semester {
    private int semesterNumber;
    private int totalEcts;

    Semester(int semesterNumber, int totalEcts){
        setSemesterNumber(semesterNumber);
        setTotalEcts(totalEcts);
    }

    // Setter
    void setSemesterNumber (int semesterNumber) {semesterNumber= this.semesterNumber;}
    void setTotalEcts (int totalEcts) {totalEcts= this.totalEcts;}

    // Getter
    int getSemesterNumber () {return semesterNumber;}
    int getTotalEcts () {return totalEcts;}


}
