package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import java.util.Optional;

public interface ScheduleRepository {

  void save(String degreeProgramId, Schedule schedule);

  Optional<Schedule> findByDegreeProgramId(String degreeProgramId);

  void delete(String degreeProgramId);
}
