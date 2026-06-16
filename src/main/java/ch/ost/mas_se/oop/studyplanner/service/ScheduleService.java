package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {

  Schedule calculate(List<StudyModule> modules);

  void calculateAndSave(DegreeProgram program);

  Optional<Schedule> findByDegreeProgramId(String degreeProgramId);

  void deleteByDegreeProgramId(String degreeProgramId);
}
