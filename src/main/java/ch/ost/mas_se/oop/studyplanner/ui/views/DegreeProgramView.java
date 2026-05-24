package ch.ost.mas_se.oop.studyplanner.ui.views;

public class DegreeProgramView {
    // Liefert Daten ans HTML-Template
    // Hat nur Getter, keine Setter
    // Enthält nur das, was die UI braucht

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
}
