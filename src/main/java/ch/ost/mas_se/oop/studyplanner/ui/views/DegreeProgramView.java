package ch.ost.mas_se.oop.studyplanner.ui.views;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;

public record DegreeProgramView(
    String degreeProgramId, String name, String description, int maxDurationInSemesters) {

  public static DegreeProgramView from(DegreeProgram program) {
    return new DegreeProgramView(
        program.getDegreeProgramId(),
        program.getName(),
        program.getDescription(),
        program.getMaxDurationInSemesters());
  }
}
