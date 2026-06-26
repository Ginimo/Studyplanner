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
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class StudyModuleServiceImpl implements StudyModuleService {
  private final StudyModuleRepository studyModuleRepository;

  public StudyModuleServiceImpl(StudyModuleRepository studyModuleRepository) {
    this.studyModuleRepository = studyModuleRepository;
  }

  @Override
  public void createStudyModule(
      String studyModuleKey,
      String degreeProgramId,
      String name,
      String description,
      int ects,
      List<String> prerequisiteKeys,
      LocalDate offeredFrom,
      LocalDate offeredTo) {
    studyModuleRepository.saveStudyModule(
        new StudyModule(
            studyModuleKey,
            degreeProgramId,
            name,
            description,
            ects,
            prerequisiteKeys,
            offeredFrom,
            offeredTo));
  }

  @Override
  public Optional<StudyModule> findStudyModuleByKey(String degreeProgramId, String studyModuleKey) {
    return studyModuleRepository.findStudyModuleByKey(degreeProgramId, studyModuleKey);
  }

  @Override
  public List<StudyModule> findAllStudyModulesByDegreeProgramId(String degreeProgramId) {
    return studyModuleRepository.findAllStudyModulesByDegreeProgramId(degreeProgramId);
  }

  @Override
  public List<StudyModule> findStudyModulesByKeys(
      String degreeProgramId, List<String> studyModuleKeys) {
    Set<String> studyModuleKeySet = new HashSet<>(studyModuleKeys);
    List<StudyModule> result = new ArrayList<>();
    for (StudyModule module :
        studyModuleRepository.findAllStudyModulesByDegreeProgramId(degreeProgramId)) {
      if (studyModuleKeySet.contains(module.getStudyModuleKey())) {
        result.add(module);
      }
    }
    return result;
  }

  @Override
  public void updateStudyModule(
      String studyModuleKey,
      String degreeProgramId,
      String name,
      String description,
      int ects,
      List<String> prerequisiteKeys,
      LocalDate offeredFrom,
      LocalDate offeredTo) {
    studyModuleRepository.saveStudyModule(
        new StudyModule(
            studyModuleKey,
            degreeProgramId,
            name,
            description,
            ects,
            prerequisiteKeys,
            offeredFrom,
            offeredTo));
  }

  @Override
  public void deleteStudyModule(String degreeProgramId, String studyModuleKey) {
    studyModuleRepository.findAllStudyModulesByDegreeProgramId(degreeProgramId).stream()
        .filter(module -> module.getPrerequisiteKeys().contains(studyModuleKey))
        .forEach(
            module ->
                studyModuleRepository.saveStudyModule(module.withoutPrerequisite(studyModuleKey)));
    studyModuleRepository.deleteStudyModule(degreeProgramId, studyModuleKey);
  }
}
