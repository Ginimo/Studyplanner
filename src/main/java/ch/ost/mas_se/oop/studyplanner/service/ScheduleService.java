package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import java.util.Optional;

public interface ScheduleService {

  Schedule calculate(DegreeProgram program);

  void calculateAndSave(DegreeProgram program);

  Optional<Schedule> findByDegreeProgramId(String degreeProgramId);

  void deleteByDegreeProgramId(String degreeProgramId);
}
