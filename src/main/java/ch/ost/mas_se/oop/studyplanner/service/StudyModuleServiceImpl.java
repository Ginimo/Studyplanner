package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.repository.StudyModuleRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class StudyModuleServiceImpl implements StudyModuleService {
  private final StudyModuleRepository repository;

  public StudyModuleServiceImpl(StudyModuleRepository repository) {
    this.repository = repository;
  }

  @Override
  public void createStudyModule(
      String key,
      String degreeProgramId,
      String name,
      String description,
      int ects,
      List<String> prerequisiteKeys,
      LocalDate offeredFrom,
      LocalDate offeredTo) {
    repository.saveStudyModule(
        new StudyModule(
            key,
            degreeProgramId,
            name,
            description,
            ects,
            prerequisiteKeys,
            offeredFrom,
            offeredTo));
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
  public List<StudyModule> findStudyModulesByKeys(String degreeProgramId, List<String> keys) {
    Set<String> keySet = new HashSet<>(keys);
    List<StudyModule> result = new ArrayList<>();
    for (StudyModule m : repository.findAllStudyModulesByDegreeProgramId(degreeProgramId)) {
      if (keySet.contains(m.getStudyModuleKey())) {
        result.add(m);
      }
    }
    return result;
  }

  @Override
  public void updateStudyModule(
      String key,
      String degreeProgramId,
      String name,
      String description,
      int ects,
      List<String> prerequisiteKeys,
      LocalDate offeredFrom,
      LocalDate offeredTo) {
    repository.saveStudyModule(
        new StudyModule(
            key,
            degreeProgramId,
            name,
            description,
            ects,
            prerequisiteKeys,
            offeredFrom,
            offeredTo));
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
