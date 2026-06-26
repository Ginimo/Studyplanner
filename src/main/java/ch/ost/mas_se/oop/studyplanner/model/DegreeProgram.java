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

  /**
   * Two degree programs are equal if and only if they have the same {@code id}. Equality is based
   * on the business key rather than all fields, so that a fetched and an in-memory instance of the
   * same program compare as equal.
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
    return Objects.equals(id, that.id);
  }

  /** Consistent with {@link #equals}: based solely on {@code id}. */
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
