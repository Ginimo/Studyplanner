package ch.ost.mas_se.oop.studyplanner.ui.views;

public class DegreeProgramView {

    private final String id;
    private final String name;
    private final String description;
    private final int maxDurationInSemesters;

    public DegreeProgramView(final String id, final String name,
                              final String description, final int maxDurationInSemesters) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxDurationInSemesters = maxDurationInSemesters;
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public int getMaxDurationInSemesters() { return maxDurationInSemesters; }

    // added for testing purposes
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DegreeProgramView that = (DegreeProgramView) o;
        return maxDurationInSemesters == that.maxDurationInSemesters &&
                java.util.Objects.equals(id, that.id) &&
                java.util.Objects.equals(name, that.name) &&
                java.util.Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, description, maxDurationInSemesters);
    }
}
