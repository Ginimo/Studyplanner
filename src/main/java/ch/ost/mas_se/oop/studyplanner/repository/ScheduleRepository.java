package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import java.util.Optional;

public interface ScheduleRepository {

  void saveSchedule(String degreeProgramId, Schedule schedule);

  Optional<Schedule> findScheduleByDegreeProgramId(String degreeProgramId);

  void deleteSchedule(String degreeProgramId);
}
