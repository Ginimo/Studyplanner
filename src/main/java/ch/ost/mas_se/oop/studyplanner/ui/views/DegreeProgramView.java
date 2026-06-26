package ch.ost.mas_se.oop.studyplanner.ui.views;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;

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

  public static DegreeProgramView toView(DegreeProgram program) {
    return new DegreeProgramView(
        program.getId(),
        program.getName(),
        program.getDescription(),
        program.getMaxDurationInSemesters());
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
}
