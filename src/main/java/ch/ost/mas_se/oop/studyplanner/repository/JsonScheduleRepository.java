package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JsonScheduleRepository implements ScheduleRepository {


  @Override
  public void save(String degreeProgramId, Schedule schedule) {

  }

  @Override
  public Optional<Schedule> findByDegreeProgramId(String degreeProgramId) {
    return Optional.empty();
  }

  @Override
  public void delete(String degreeProgramId) {

  }
}
