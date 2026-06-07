package ch.ost.mas_se.oop.studyplanner.model;

import java.util.List;

public class Semester {
    private int semesterNumber;
    private int totalEcts;
    private List<StudyModule> studyModulesSemester;
    private List<String>  studyModuleKeys;
    private Schedule schedule;

    public Semester(int semesterNumber,
                    int totalEcts,
                    List<StudyModule>  studyModulesSemester,
                    List<String> studyModuleKeys,
                    Schedule schedule
                    ){
        setSemesterNumber(semesterNumber);
        setTotalEcts(totalEcts);
        this.studyModulesSemester = studyModulesSemester;
        this.studyModuleKeys = studyModuleKeys;
        this.schedule = schedule;
    }

    // Setter
    public void setSemesterNumber (int semesterNumber) {this.semesterNumber=semesterNumber;}
    public void setTotalEcts (int totalEcts) {this.totalEcts=totalEcts;}

    // Getter
    public int getSemesterNumber () {return semesterNumber;}
    public int getTotalEcts () {return totalEcts;}
    public String getDegreeProgramID(){return schedule.getDegreeProgramID();}

    public List<StudyModule> getStudyModulesSemester(){return studyModulesSemester;}
    public List<String> getStudyModuleKeys(){return studyModuleKeys;}
    public Schedule getSchedule(){return schedule;}

    // Printer
    public void printSemester(){
        IO.println("Semester configured as: ");
        IO.println("*******************" );
        IO.println("Semester: " + semesterNumber);
        IO.println("Total ECTS in this Semester: " + totalEcts);
        IO.println("Planned Study Modules in this Semester: ");
        IO.println("*******************");
        for (StudyModule module : studyModulesSemester){
            module.printStudyModule();;
        }
        IO.println("*******************");
    }

}
