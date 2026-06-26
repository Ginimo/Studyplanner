package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  protected String fileName() {
    return "degree-programs.json";
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
      if (list.get(i).getDegreeProgramId().equals(program.getDegreeProgramId())) {
        list.set(i, program);
        store(list);
        return;
      }
    }
    list.add(program);
    store(list);
  }

  @Override
  public Optional<DegreeProgram> findDegreeProgramById(String degreeProgramId) {
    for (DegreeProgram program : load()) {
      if (program.getDegreeProgramId().equals(degreeProgramId)) return Optional.of(program);
    }
    return Optional.empty();
  }

  @Override
  public List<DegreeProgram> findAllDegreePrograms() {
    return load();
  }

  @Override
  public void deleteDegreeProgram(String degreeProgramId) {
    List<DegreeProgram> list = load();
    list.removeIf(program -> program.getDegreeProgramId().equals(degreeProgramId));
    store(list);
  }
}
