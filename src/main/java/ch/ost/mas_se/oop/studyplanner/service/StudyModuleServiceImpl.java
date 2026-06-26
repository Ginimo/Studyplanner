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
  public void create(StudyModule module) {
    repository.save(module);
  }

  @Override
  public Optional<StudyModule> findByKey(String degreeProgramId, String key) {
    return repository.findByKey(degreeProgramId, key);
  }

  @Override
  public List<StudyModule> findAllByDegreeProgramId(String degreeProgramId) {
    return repository.findAllByDegreeProgramId(degreeProgramId);
  }

  @Override
  public void update(StudyModule module) {
    repository.save(module);
  }

  @Override
  public void delete(String degreeProgramId, String key) {
    repository.findAllByDegreeProgramId(degreeProgramId).stream()
        .filter(m -> m.getPrerequisiteKeys().contains(key))
        .forEach(
            m -> {
              List<String> updatedPrereqs = new ArrayList<>(m.getPrerequisiteKeys());
              updatedPrereqs.remove(key);
              repository.save(
                  new StudyModule(
                      m.getStudyModuleKey(),
                      m.getDegreeProgramId(),
                      m.getName(),
                      m.getDescription(),
                      m.getEcts(),
                      updatedPrereqs,
                      m.getOfferedFrom(),
                      m.getOfferedTo()));
            });
    repository.delete(degreeProgramId, key);
  }
}
