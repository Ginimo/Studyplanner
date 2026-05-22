package ch.ost.mas_se.oop.studyplanner.ui.forms;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Form-binding object for creating and editing a degree program.
 *
 * <p>This is a web-layer DTO used by Spring MVC to bind HTML form fields.
 * It is intentionally separate from the {@code DegreeProgram} domain object,
 * which is immutable and validated in its constructor.</p>
 */
public class DegreeProgramForm {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @Min(value = 1, message = "Max duration must be at least 1 semester")
    private int maxDurationInSemesters;

    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(final String description) { this.description = description; }

    public int getMaxDurationInSemesters() { return maxDurationInSemesters; }
    public void setMaxDurationInSemesters(final int maxDurationInSemesters) {
        this.maxDurationInSemesters = maxDurationInSemesters;
    }
}
