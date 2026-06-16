package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

abstract class AbstractController {

  protected final DegreeProgramService degreeProgramService;

  protected AbstractController(DegreeProgramService degreeProgramService) {
    this.degreeProgramService = degreeProgramService;
  }

  protected DegreeProgram requireProgram(String id) {
    return degreeProgramService
        .findById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Degree program not found: " + id));
  }

  protected DegreeProgramView toView(DegreeProgram program) {
    return new DegreeProgramView(
        program.getId(),
        program.getName(),
        program.getDescription(),
        program.getMaxDurationInSemesters());
  }
}
