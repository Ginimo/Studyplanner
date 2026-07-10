package ch.ost.mas_se.oop.studyplanner.model;

import java.util.Objects;

public class DegreeProgram {
  private final String degreeProgramId;
  private final String name;
  private final String description;
  private final int maxDurationInSemesters;


  public DegreeProgram(
      String degreeProgramId, String name, String description, int maxDurationInSemesters) {
    this.degreeProgramId = Objects.requireNonNull(degreeProgramId);
    this.name = Objects.requireNonNull(name);
    this.description = description;
    if (maxDurationInSemesters < 1) {
      throw new IllegalArgumentException(
          "maxDurationInSemesters must be at least 1, got: " + maxDurationInSemesters);
    }
    this.maxDurationInSemesters = maxDurationInSemesters;
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

  public int getMaxDurationInSemesters() {
    return maxDurationInSemesters;
  }

  /**
   * Two degree programs are equal if and only if they have the same {@code degreeProgramId}.
   * Equality is based on the business key rather than all fields, so that a fetched and an
   * in-memory instance of the same program compare as equal.
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    DegreeProgram that = (DegreeProgram) other;
    return Objects.equals(degreeProgramId, that.degreeProgramId);
  }

  /** Consistent with {@link #equals}: based solely on {@code degreeProgramId}. */
  @Override
  public int hashCode() {
    return Objects.hash(degreeProgramId);
  }
}
