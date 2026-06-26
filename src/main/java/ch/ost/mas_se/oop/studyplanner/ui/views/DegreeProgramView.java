package ch.ost.mas_se.oop.studyplanner.ui.views;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;

public class DegreeProgramView {

  private final String degreeProgramId;
  private final String name;
  private final String description;
  private final int maxDurationInSemesters;

  public DegreeProgramView(
      final String degreeProgramId,
      final String name,
      final String description,
      final int maxDurationInSemesters) {
    this.degreeProgramId = degreeProgramId;
    this.name = name;
    this.description = description;
    this.maxDurationInSemesters = maxDurationInSemesters;
  }

  public static DegreeProgramView from(DegreeProgram program) {
    return new DegreeProgramView(
        program.getDegreeProgramId(),
        program.getName(),
        program.getDescription(),
        program.getMaxDurationInSemesters());
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
}
