package ch.ost.mas_se.oop.studyplanner.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class StudyModule {
    private final String studyModuleKey;
    private final String degreeProgramId;
    private final String name;
    private final String description;
    private final int ects;
    private final List<String> prerequisiteKeys;
    private final LocalDate offeredFrom;
    private final LocalDate offeredTo;

    public StudyModule(String studyModuleKey, String degreeProgramId, String name, String description, int ects, List<String> prerequisiteKeys, LocalDate offeredFrom, LocalDate offeredTo) {
        this.studyModuleKey = Objects.requireNonNull(studyModuleKey);
        this.degreeProgramId = Objects.requireNonNull(degreeProgramId);
        this.name = Objects.requireNonNull(name);
        this.description = description;
        this.ects = ects;
        this.prerequisiteKeys = List.copyOf(prerequisiteKeys != null ? prerequisiteKeys : List.of());
        this.offeredFrom = offeredFrom;
        this.offeredTo = offeredTo;
    }

    public String getStudyModuleKey() {
        return studyModuleKey;
    }

    public String getDegreeProgramId() {
        return degreeProgramId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getEcts() {
        return ects;
    }

    public List<String> getPrerequisiteKeys() {
        return prerequisiteKeys;
    }

    public LocalDate getOfferedFrom() {
        return offeredFrom;
    }

    public LocalDate getOfferedTo() {
        return offeredTo;
    }

    // if a class has a meaningful business identity (an ID field that uniquely identifies it),
    // override equals/hashCode based on that field
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StudyModule that = (StudyModule) o;
        return Objects.equals(studyModuleKey, that.studyModuleKey)
                && Objects.equals(degreeProgramId, that.degreeProgramId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studyModuleKey, degreeProgramId);
    }
}
