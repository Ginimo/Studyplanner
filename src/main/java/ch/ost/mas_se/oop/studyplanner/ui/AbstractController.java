package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

abstract class AbstractController {

  protected final DegreeProgramService degreeProgramService;

  protected AbstractController(DegreeProgramService degreeProgramService) {
    this.degreeProgramService = degreeProgramService;
  }

  protected DegreeProgram requireDegreeProgram(String degreeProgramId) {
    return degreeProgramService
        .findDegreeProgramById(degreeProgramId)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Degree program not found: " + degreeProgramId));
  }
}
