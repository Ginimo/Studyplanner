package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {

  Schedule calculateSchedule(List<StudyModule> modules);

  void calculateAndSaveSchedule(DegreeProgram program);

  Optional<Schedule> findScheduleByDegreeProgramId(String degreeProgramId);

  void deleteSchedule(String degreeProgramId);
}
