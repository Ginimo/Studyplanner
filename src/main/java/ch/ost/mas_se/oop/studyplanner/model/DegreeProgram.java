package ch.ost.mas_se.oop.studyplanner.model;

import java.util.Objects;

public class DegreeProgram {
    private final String id;
    private final String name;
    private final String description;
    private final int maxDurationInSemesters;

    public DegreeProgram(String id, String name, String description, int maxDurationInSemesters) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.description = description;
        this.maxDurationInSemesters = maxDurationInSemesters;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxDurationInSemesters() {
        return maxDurationInSemesters;
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
        DegreeProgram that = (DegreeProgram) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
