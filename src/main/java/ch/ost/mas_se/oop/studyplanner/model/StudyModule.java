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

  /**
   * @param studyModuleKey unique key within the degree program; must not be null
   * @param degreeProgramId ID of the owning degree program; must not be null
   * @param name human-readable module name; must not be null
   * @param description optional description; may be null
   * @param ects ECTS credit points for this module
   * @param prerequisiteKeys keys of modules that must be completed first; null is treated as empty
   * @param offeredFrom date from which the module is offered; may be null
   * @param offeredTo date until which the module is offered; null means open-ended
   */
  public StudyModule(
      String studyModuleKey,
      String degreeProgramId,
      String name,
      String description,
      int ects,
      List<String> prerequisiteKeys,
      LocalDate offeredFrom,
      LocalDate offeredTo) {
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

  /**
   * Two study modules are equal if and only if they share the same {@code degreeProgramId} and
   * {@code studyModuleKey} — the composite business key that uniquely identifies a module within a
   * degree program.
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    StudyModule that = (StudyModule) other;
    return Objects.equals(studyModuleKey, that.studyModuleKey)
        && Objects.equals(degreeProgramId, that.degreeProgramId);
  }

  /**
   * Consistent with {@link #equals}: based solely on {@code studyModuleKey} and {@code
   * degreeProgramId}.
   */
  @Override
  public int hashCode() {
    return Objects.hash(studyModuleKey, degreeProgramId);
  }
}
