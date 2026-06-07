package ch.ost.mas_se.oop.studyplanner.model;
import java.time.LocalDate;
import java.util.List;

public class StudyModule {
    private final String studyModuleKey;
    private final DegreeProgram degreeProgram;  // ← neu

    private String name;
    private String description;
    private int ects;
    private List<String> prerequisiteKeys;
    private LocalDate offeredFrom;
    private LocalDate offeredTo;

    public StudyModule(String studyModuleKey, String name, String description, int ects, List<String> prerequisiteKeys,
                LocalDate offeredFrom, LocalDate offeredTo, DegreeProgram degreeProgram) {
        this.studyModuleKey = studyModuleKey; // nicht veränderbar
        setName(name);
        setDescription(description);
        setEcts(ects);
        setPrerequisiteKeys(prerequisiteKeys);
        setOfferedFrom(offeredFrom);
        setOfferedTo(offeredTo);
        this.degreeProgram = degreeProgram;
    }

    // Setter
    //void setStudyModuleKey (String studyModuleKey) {this.studyModuleKey=studyModuleKey;}
    public void setName (String name) {this.name=name;}
    public void setDescription (String description) {this.description=description;}
    public void setEcts (int ects) {this.ects=ects;}
    public void setPrerequisiteKeys(List<String> prerequisiteKeys) {this.prerequisiteKeys=prerequisiteKeys;};
    public void setOfferedFrom(LocalDate offeredFrom) {this.offeredFrom=offeredFrom;};
    public void setOfferedTo(LocalDate offeredTo) {this.offeredTo=offeredTo;};
    public boolean hasPrerequisites() {
        return prerequisiteKeys != null && !prerequisiteKeys.isEmpty();
    }


    // Getter
    public String getStudyModuleKey () {return studyModuleKey;}
    public String getName () {return name;}
    public String getDescription () {return description;}
    public int getEcts() {return ects;}
    public List<String> getPrerequisiteKeys () {return prerequisiteKeys;}
    public LocalDate getOfferedFrom() {return offeredFrom;}
    public LocalDate getOfferedTo() {return offeredTo;}
    public DegreeProgram getDegreeProgram() { return degreeProgram; }
    public int getPrerequisiteCount() {return prerequisiteKeys == null ? 0 : prerequisiteKeys.size();}

    // Printer
    public void printStudyModule(){
        IO.println("Study Module configured as: ");
        IO.println("*******************" );
        IO.println("studyModuleKey: " + studyModuleKey);
        IO.println("Name: " + name);
        IO.println("Description: " + description);
        IO.println("ECTS: " + ects);
        IO.println("PrerequisiteKeys: " + prerequisiteKeys);
        IO.println("OfferedFrom: " + offeredFrom);
        IO.println("OfferedTo: " + offeredTo);
        IO.println("Degree Program: " + degreeProgram.getId());
        IO.println("*******************");
    }

    // Equals
    @Override
    public boolean equals(Object obj) {
        // Ein Study Module ist gleich, wenn es dasselbe Objekt ist.
        if (this == obj) return true;                    // selbes Objekt
        if (obj == null) return false;                   // null check
        if (getClass() != obj.getClass()) return false;  // selber Typ

        StudyModule other = (StudyModule) obj;
        return studyModuleKey.equals(other.studyModuleKey);
    }

    @Override
    public int hashCode() {
        return studyModuleKey.hashCode();
    }
}
