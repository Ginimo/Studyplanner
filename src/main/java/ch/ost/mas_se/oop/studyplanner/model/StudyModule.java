package ch.ost.mas_se.oop.studyplanner.model;
import java.time.LocalDate;
import java.util.List;

public class StudyModule {
    private String studyModuleKey;
    private String name;
    private String description;
    private int ects;
    private List<String> prerequisiteKeys;
    private LocalDate offeredFrom;
    private LocalDate offeredTo;

    StudyModule(String studyModuleKey, String name, String description, int ects, List<String> prerequisiteKeys,
                LocalDate offeredFrom, LocalDate offeredTo) {
        setStudyModuleKey(studyModuleKey);
        setName(name);
        setDescription(description);
        setEcts(ects);
        setPrerequisiteKeys(prerequisiteKeys);
        setOfferedFrom(offeredFrom);
        setOfferedTo(offeredTo);
    }


    // Setter
    void setStudyModuleKey (String studyModuleKey) {studyModuleKey= this.studyModuleKey;}
    void setName (String name) {name= this.name;}
    void setDescription (String description) {description= this.description;}
    void setEcts (int ects) {ects= this.ects;}
    void setPrerequisiteKeys(List<String> prerequisiteKeys) {prerequisiteKeys= this.prerequisiteKeys;};
    void setOfferedFrom(LocalDate offeredFrom) {offeredFrom= this.offeredFrom;};
    void setOfferedTo(LocalDate offeredTo) {offeredTo= this.offeredTo;};

    // Getter
    String getStudyModuleKey () {return studyModuleKey;}
    String getName () {return name;}
    String getDescription () {return description;}
    int getEcts() {return ects;}
    List<String> getPrerequisiteKeys () {return prerequisiteKeys;}
    LocalDate getOfferedFrom() {return offeredFrom;}
    LocalDate getOfferedTo() {return offeredTo;}


}
