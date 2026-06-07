package ch.ost.mas_se.oop.studyplanner.model;

public class DegreeProgram {
    private final String id;
    private String name;
    private String description;
    private int maxDurationInSemesters;
    private Schedule schedule;

    public DegreeProgram(String id, String name, String description, int maxDurationInSemesters){
        this.id=id;
        setName(name);
        setDescription(description);
        setMaxDurationInSemesters(maxDurationInSemesters);
    }

    // Setter
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

    // Equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                         // 1. gleiches Objekt im Speicher?
        if (!(o instanceof DegreeProgram)) return false;    // 2. überhaupt ein DegreeProgram?
        DegreeProgram other = (DegreeProgram) o;            // 3. casten
        return this.id.equals(other.id);                    // 4. gleiche ID?
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
