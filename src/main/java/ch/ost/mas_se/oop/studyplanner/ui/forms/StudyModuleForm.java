package ch.ost.mas_se.oop.studyplanner.ui.forms;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class StudyModuleForm {

  @NotBlank(message = "Study module key is required")
  private String studyModuleKey;

  @NotBlank(message = "Name is required")
  private String name;

  private String description;

  @NotNull(message = "ECTS must be at least 1")
  @Min(value = 1, message = "ECTS must be at least 1")
  private Integer ects;

  private List<String> prerequisiteKeys = new ArrayList<>();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate offeredFrom;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate offeredTo;

  private boolean newEntry;

  public String getStudyModuleKey() {
    return studyModuleKey;
  }

  public void setStudyModuleKey(final String studyModuleKey) {
    this.studyModuleKey = studyModuleKey;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public Integer getEcts() {
    return ects;
  }

  public void setEcts(final Integer ects) {
    this.ects = ects;
  }

  public List<String> getPrerequisiteKeys() {
    return prerequisiteKeys;
  }

  public void setPrerequisiteKeys(final List<String> prerequisiteKeys) {
    this.prerequisiteKeys = prerequisiteKeys != null ? prerequisiteKeys : new ArrayList<>();
  }

  public LocalDate getOfferedFrom() {
    return offeredFrom;
  }

  public void setOfferedFrom(final LocalDate offeredFrom) {
    this.offeredFrom = offeredFrom;
  }

  public LocalDate getOfferedTo() {
    return offeredTo;
  }

  public void setOfferedTo(final LocalDate offeredTo) {
    this.offeredTo = offeredTo;
  }

  @AssertTrue(message = "Offered-to date must not be before offered-from date")
  public boolean isOfferedToAfterOfferedFrom() {
    if (offeredFrom == null || offeredTo == null) return true;
    return !offeredTo.isBefore(offeredFrom);
  }

  public boolean isNewEntry() {
    return newEntry;
  }

  public void setNewEntry(final boolean newEntry) {
    this.newEntry = newEntry;
  }
}
