package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import java.util.Optional;

public interface ScheduleService {

  void calculateAndSaveSchedule(DegreeProgram program);

  Optional<Schedule> findScheduleByDegreeProgramId(String degreeProgramId);

  void deleteSchedule(String degreeProgramId);
}
