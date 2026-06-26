package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
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
public class JsonDegreeProgramRepository implements DegreeProgramRepository {

  private final Path file = Path.of("data", "degree-programs.json");
  private final ObjectMapper mapper;

  public JsonDegreeProgramRepository(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public void saveDegreeProgram(DegreeProgram program) {
    List<DegreeProgram> list = load();
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).getId().equals(program.getId())) {
        list.set(i, program);
        store(list);
        return;
      }
    }
    list.add(program);
    store(list);
  }

  @Override
  public Optional<DegreeProgram> findByDegreeProgramId(String id) {
    for (DegreeProgram p : load()) {
      if (p.getId().equals(id)) return Optional.of(p);
    }
    return Optional.empty();
  }

  @Override
  public List<DegreeProgram> findAllDegreePrograms() {
    return load();
  }

  @Override
  public void deleteDegreeProgram(String id) {
    List<DegreeProgram> list = load();
    list.removeIf(p -> p.getId().equals(id));
    store(list);
  }

  private List<DegreeProgram> load() {
    if (!Files.exists(file)) return new ArrayList<>();
    try {
      return mapper.readValue(file.toFile(), new TypeReference<>() {});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void store(List<DegreeProgram> list) {
    try {
      Files.createDirectories(file.getParent());
      mapper.writeValue(file.toFile(), list);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
