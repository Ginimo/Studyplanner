package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JsonStudyModuleRepository implements StudyModuleRepository {

  private final Path file = Path.of("data", "study-modules.json");
  private final ObjectMapper mapper;

  public JsonStudyModuleRepository(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public void save(StudyModule module) {
    List<StudyModule> list = load();
    for (int i = 0; i < list.size(); i++) {
      StudyModule m = list.get(i);
      if (m.getDegreeProgramId().equals(module.getDegreeProgramId())
          && m.getStudyModuleKey().equals(module.getStudyModuleKey())) {
        list.set(i, module);
        store(list);
        return;
      }
    }
    list.add(module);
    store(list);
  }

  @Override
  public Optional<StudyModule> findByKey(String degreeProgramId, String key) {
    for (StudyModule m : load()) {
      if (m.getDegreeProgramId().equals(degreeProgramId) && m.getStudyModuleKey().equals(key)) {
        return Optional.of(m);
      }
    }
    return Optional.empty();
  }

  @Override
  public List<StudyModule> findAllByDegreeProgramId(String degreeProgramId) {
    List<StudyModule> result = new ArrayList<>();
    for (StudyModule m : load()) {
      if (m.getDegreeProgramId().equals(degreeProgramId)) result.add(m);
    }
    return result;
  }

  @Override
  public void delete(String degreeProgramId, String key) {
    List<StudyModule> list = load();
    list.removeIf(
        m -> m.getDegreeProgramId().equals(degreeProgramId) && m.getStudyModuleKey().equals(key));
    store(list);
  }

  private List<StudyModule> load() {
    if (!Files.exists(file)) return new ArrayList<>();
    try {
      return mapper.readValue(file.toFile(), new TypeReference<>() {});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void store(List<StudyModule> list) {
    try {
      Files.createDirectories(file.getParent());
      mapper.writeValue(file.toFile(), list);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
