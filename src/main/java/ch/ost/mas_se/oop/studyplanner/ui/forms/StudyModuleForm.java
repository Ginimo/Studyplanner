package ch.ost.mas_se.oop.studyplanner.ui.forms;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Form-binding object for creating and editing a study module.
 *
 * <p>This is a web-layer DTO used by Spring MVC to bind HTML form fields.
 * It is intentionally separate from the {@code StudyModule} domain object,
 * which is immutable and validated in its constructor.</p>
 */
public class StudyModuleForm {

    @NotBlank(message = "Study module key is required")
    private String studyModuleKey;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @Min(value = 1, message = "ECTS must be at least 1")
    private int ects;

    private List<String> prerequisiteKeys = new ArrayList<>(); // nur die Keys (z.B. DB1)

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate offeredFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate offeredTo;

    private boolean isNew;

    public String getStudyModuleKey() { return studyModuleKey; }
    public void setStudyModuleKey(final String studyModuleKey) { this.studyModuleKey = studyModuleKey; }

    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(final String description) { this.description = description; }

    public int getEcts() { return ects; }
    public void setEcts(final int ects) { this.ects = ects; }

    public List<String> getPrerequisiteKeys() { return prerequisiteKeys; }
    public void setPrerequisiteKeys(final List<String> prerequisiteKeys) {
        this.prerequisiteKeys = prerequisiteKeys != null ? prerequisiteKeys : new ArrayList<>();
    }

    public LocalDate getOfferedFrom() { return offeredFrom; }
    public void setOfferedFrom(final LocalDate offeredFrom) { this.offeredFrom = offeredFrom; }

    public LocalDate getOfferedTo() { return offeredTo; }
    public void setOfferedTo(final LocalDate offeredTo) { this.offeredTo = offeredTo; }

    public boolean getIsNew() { return isNew; }
    public void setIsNew(final boolean isNew) { this.isNew = isNew; }
}
