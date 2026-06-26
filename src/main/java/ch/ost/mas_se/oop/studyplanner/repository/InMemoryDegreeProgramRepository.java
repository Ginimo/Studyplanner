package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryDegreeProgramRepository implements DegreeProgramRepository {

  private final Map<String, DegreeProgram> store = new LinkedHashMap<>();

  @Override
  public void save(DegreeProgram program) {
    store.put(program.getId(), program);
  }

  @Override
  public Optional<DegreeProgram> findById(String id) {
    return Optional.ofNullable(store.get(id));
  }

  @Override
  public List<DegreeProgram> findAll() {
    return new ArrayList<>(store.values());
  }

  @Override
  public void delete(String id) {
    store.remove(id);
  }
}
