package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.repository.DegreeProgramRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class DegreeProgramServiceImpl implements DegreeProgramService {
  private final DegreeProgramRepository degreeProgramRepository;
  private final StudyModuleService studyModuleService;

  public DegreeProgramServiceImpl(
      DegreeProgramRepository degreeProgramRepository, StudyModuleService studyModuleService) {
    this.degreeProgramRepository = degreeProgramRepository;
    this.studyModuleService = studyModuleService;
  }

  @Override
  public DegreeProgram createDegreeProgram(
      String name, String description, int maxDurationInSemesters) {
    DegreeProgram program =
        new DegreeProgram(UUID.randomUUID().toString(), name, description, maxDurationInSemesters);
    degreeProgramRepository.saveDegreeProgram(program);
    return program;
  }

  @Override
  public Optional<DegreeProgram> findDegreeProgramById(String degreeProgramId) {
    return degreeProgramRepository.findDegreeProgramById(degreeProgramId);
  }

  @Override
  public List<DegreeProgram> findAllDegreePrograms() {
    return degreeProgramRepository.findAllDegreePrograms();
  }

  @Override
  public void updateDegreeProgram(
      String degreeProgramId, String name, String description, int maxDurationInSemesters) {
    degreeProgramRepository.saveDegreeProgram(
        new DegreeProgram(degreeProgramId, name, description, maxDurationInSemesters));
  }

  @Override
  public void deleteDegreeProgram(String degreeProgramId) {
    studyModuleService.deleteAllStudyModulesByDegreeProgramId(degreeProgramId);
    degreeProgramRepository.deleteDegreeProgram(degreeProgramId);
  }
}
