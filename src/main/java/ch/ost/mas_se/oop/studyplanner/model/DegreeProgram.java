package ch.ost.mas_se.oop.studyplanner.model;

public class DegreeProgram {
    private String id;
    private String name;
    private String description;
    private int maxDurationInSemesters;
    private Schedule schedule;

    public DegreeProgram(String id, String name, String description, int maxDurationInSemesters){
        setID(id);
        setName(name);
        setDescription(description);
        setMaxDurationInSemesters(maxDurationInSemesters);
    }

    // Setter
    public void setID (String id) {this.id=id;}
    public void setName (String name) {this.name= name;}
    public void setDescription (String description) {this.description=description;}
    public void setMaxDurationInSemesters (int maxDurationInSemesters) {this.maxDurationInSemesters=maxDurationInSemesters;}

    // Getter
    public String getId () {return id;}
    public String getName () {return name;}
    public String getDescription () {return description;}
    public int getMaxDurationInSemesters ( ) {return maxDurationInSemesters;}

    // Printer
    public void printDegreeProgram(){
        IO.println("Degree Program configured as: ");
        IO.println("*******************" );
        IO.println("ID: " + id);
        IO.println("Name: " + name);
        IO.println("Description: " + description);
        IO.println("Duration: " + maxDurationInSemesters);
        IO.println("*******************");
    }

}
