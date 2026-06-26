package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JsonScheduleRepository extends AbstractJsonRepository<Map<String, Schedule>>
    implements ScheduleRepository {

  public JsonScheduleRepository(ObjectMapper mapper) {
    super(mapper);
  }

  @Override
  protected String fileName() {
    return "schedules.json";
  }

  @Override
  protected TypeReference<Map<String, Schedule>> typeRef() {
    return new TypeReference<>() {};
  }

  @Override
  protected Map<String, Schedule> emptyValue() {
    return new HashMap<>();
  }

  @Override
  public void saveSchedule(String degreeProgramId, Schedule schedule) {
    Map<String, Schedule> map = load();
    map.put(degreeProgramId, schedule);
    store(map);
  }

  @Override
  public Optional<Schedule> findScheduleByDegreeProgramId(String degreeProgramId) {
    Schedule schedule = load().get(degreeProgramId);
    if (schedule == null) return Optional.empty();
    return Optional.of(schedule);
  }

  @Override
  public void deleteSchedule(String degreeProgramId) {
    Map<String, Schedule> map = load();
    map.remove(degreeProgramId);
    store(map);
  }
}
