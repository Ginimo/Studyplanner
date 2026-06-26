package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JsonDegreeProgramRepository extends AbstractJsonRepository<List<DegreeProgram>>
    implements DegreeProgramRepository {

  public JsonDegreeProgramRepository(ObjectMapper mapper) {
    super(mapper);
  }

  @Override
  protected Path filePath() {
    return Path.of("data", "degree-programs.json");
  }

  @Override
  protected TypeReference<List<DegreeProgram>> typeRef() {
    return new TypeReference<>() {};
  }

  @Override
  protected List<DegreeProgram> emptyValue() {
    return new ArrayList<>();
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
  public Optional<DegreeProgram> findDegreeProgramById(String id) {
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
}
