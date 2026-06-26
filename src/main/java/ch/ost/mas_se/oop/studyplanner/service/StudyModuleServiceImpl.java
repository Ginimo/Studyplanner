package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.repository.StudyModuleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class StudyModuleServiceImpl implements StudyModuleService {
  private final StudyModuleRepository repository;

  public StudyModuleServiceImpl(StudyModuleRepository repository) {
    this.repository = repository;
  }

  @Override
  public void createStudyModule(StudyModule module) {
    repository.saveStudyModule(module);
  }

  @Override
  public Optional<StudyModule> findStudyModuleByKey(String degreeProgramId, String key) {
    return repository.findStudyModuleByKey(degreeProgramId, key);
  }

  @Override
  public List<StudyModule> findAllStudyModulesByDegreeProgramId(String degreeProgramId) {
    return repository.findAllStudyModulesByDegreeProgramId(degreeProgramId);
  }

  @Override
  public void updateStudyModules(StudyModule module) {
    repository.saveStudyModule(module);
  }

  @Override
  public void deleteStudyModule(String degreeProgramId, String key) {
    repository.findAllStudyModulesByDegreeProgramId(degreeProgramId).stream()
        .filter(m -> m.getPrerequisiteKeys().contains(key))
        .forEach(m -> removePrerequisiteAndSave(m, key));
    repository.deleteStudyModule(degreeProgramId, key);
  }

  private void removePrerequisiteAndSave(StudyModule module, String prerequisiteKey) {
    List<String> updatedPrereqs = new ArrayList<>(module.getPrerequisiteKeys());
    updatedPrereqs.remove(prerequisiteKey);
    repository.saveStudyModule(
        new StudyModule(
            module.getStudyModuleKey(),
            module.getDegreeProgramId(),
            module.getName(),
            module.getDescription(),
            module.getEcts(),
            updatedPrereqs,
            module.getOfferedFrom(),
            module.getOfferedTo()));
  }
}
