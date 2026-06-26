package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JsonStudyModuleRepository extends AbstractJsonRepository<List<StudyModule>>
    implements StudyModuleRepository {

  public JsonStudyModuleRepository(ObjectMapper mapper) {
    super(mapper);
  }

  @Override
  protected Path filePath() {
    return Path.of("data", "study-modules.json");
  }

  @Override
  protected TypeReference<List<StudyModule>> typeRef() {
    return new TypeReference<>() {};
  }

  @Override
  protected List<StudyModule> emptyValue() {
    return new ArrayList<>();
  }

  @Override
  public void saveStudyModule(StudyModule module) {
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
  public Optional<StudyModule> findStudyModuleByKey(String degreeProgramId, String key) {
    for (StudyModule m : load()) {
      if (m.getDegreeProgramId().equals(degreeProgramId) && m.getStudyModuleKey().equals(key)) {
        return Optional.of(m);
      }
    }
    return Optional.empty();
  }

  @Override
  public List<StudyModule> findAllStudyModulesByDegreeProgramId(String degreeProgramId) {
    List<StudyModule> result = new ArrayList<>();
    for (StudyModule m : load()) {
      if (m.getDegreeProgramId().equals(degreeProgramId)) result.add(m);
    }
    return result;
  }

  @Override
  public void deleteStudyModule(String degreeProgramId, String key) {
    List<StudyModule> list = load();
    list.removeIf(
        m -> m.getDegreeProgramId().equals(degreeProgramId) && m.getStudyModuleKey().equals(key));
    store(list);
  }
}
