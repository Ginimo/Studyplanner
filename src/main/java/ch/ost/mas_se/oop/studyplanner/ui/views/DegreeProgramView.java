package ch.ost.mas_se.oop.studyplanner.ui.views;

public class DegreeProgramView {

  private final String id;
  private final String name;
  private final String description;
  private final int maxDurationInSemesters;

  public DegreeProgramView(
      final String id,
      final String name,
      final String description,
      final int maxDurationInSemesters) {
    this.id = id;
    this.name = name;
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
   * Two views are equal if and only if they have the same {@code id}, consistent with {@link
   * ch.ost.mas_se.oop.studyplanner.model.DegreeProgram#equals}.
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    DegreeProgramView that = (DegreeProgramView) other;
    return java.util.Objects.equals(id, that.id);
  }

  /** Consistent with {@link #equals}: based solely on {@code id}. */
  @Override
  public int hashCode() {
    return java.util.Objects.hash(id);
  }
}
