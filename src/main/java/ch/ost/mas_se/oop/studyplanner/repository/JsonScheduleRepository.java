package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JsonScheduleRepository implements ScheduleRepository {

  private final Path file = Path.of("data", "schedules.json");
  private final ObjectMapper mapper;

  public JsonScheduleRepository(ObjectMapper mapper) {
    this.mapper = mapper;
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

  private Map<String, Schedule> load() {
    if (!Files.exists(file)) return new HashMap<>();
    try {
      return mapper.readValue(file.toFile(), new TypeReference<HashMap<String, Schedule>>() {});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void store(Map<String, Schedule> map) {
    try {
      Files.createDirectories(file.getParent());
      mapper.writeValue(file.toFile(), map);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
