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
      StudyModule existing = list.get(i);
      if (existing.getDegreeProgramId().equals(module.getDegreeProgramId())
          && existing.getStudyModuleKey().equals(module.getStudyModuleKey())) {
        list.set(i, module);
        store(list);
        return;
      }
    }
    list.add(module);
    store(list);
  }

  @Override
  public Optional<StudyModule> findStudyModuleByKey(String degreeProgramId, String studyModuleKey) {
    for (StudyModule module : load()) {
      if (module.getDegreeProgramId().equals(degreeProgramId)
          && module.getStudyModuleKey().equals(studyModuleKey)) {
        return Optional.of(module);
      }
    }
    return Optional.empty();
  }

  @Override
  public List<StudyModule> findAllStudyModulesByDegreeProgramId(String degreeProgramId) {
    List<StudyModule> result = new ArrayList<>();
    for (StudyModule module : load()) {
      if (module.getDegreeProgramId().equals(degreeProgramId)) result.add(module);
    }
    return result;
  }

  @Override
  public void deleteStudyModule(String degreeProgramId, String studyModuleKey) {
    List<StudyModule> list = load();
    list.removeIf(
        module ->
            module.getDegreeProgramId().equals(degreeProgramId)
                && module.getStudyModuleKey().equals(studyModuleKey));
    store(list);
  }
}
