package ch.ost.mas_se.oop.studyplanner.model;

public class DegreeProgram {
    private String id;
    private String name;
    private String description;
    private int maxDurationInSemesters;
    private Schedule schedule;

    DegreeProgram(String id, String name, String description, int maxDurationInSemesters){
        setID(id);
        setName(name);
        setDescription(description);
        setMaxDurationInSemesters(maxDurationInSemesters);
    }

    // Setter
    void setID (String id) {id= this.id;}
    void setName (String name) {name= this.name;}
    void setDescription (String description) {description= this.description;}
    void setMaxDurationInSemesters (int maxDurationInSemesters) {maxDurationInSemesters= this.maxDurationInSemesters;}

    // Getter
    String getID () {return id;}
    String getName () {return name;}
    String getDescription () {return description;}
    int getMaxDurationInSemesters ( ) {return maxDurationInSemesters;}

}
