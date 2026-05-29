package ch.ost.mas_se.oop.studyplanner.model;

public class DegreeProgram {
    private final String id;
    private String name;
    private String description;
    private int maxDurationInSemesters;

    DegreeProgram(String id){
        setID(id);
    }

    // Setter
    void setID (String id) {id= this.id;}
    void setName (String name) {name= this.name;}
    void setDescription (String description) {description= this.description;}
    void setMaxDurationInSemesters (int maxDurationInSemesters) {name= this.name;}

}
