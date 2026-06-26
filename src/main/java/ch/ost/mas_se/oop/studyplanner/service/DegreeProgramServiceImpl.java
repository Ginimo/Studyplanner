package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.repository.DegreeProgramRepository;
import ch.ost.mas_se.oop.studyplanner.repository.ScheduleRepository;
import ch.ost.mas_se.oop.studyplanner.repository.StudyModuleRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class DegreeProgramServiceImpl implements DegreeProgramService {
  private final DegreeProgramRepository degreeProgramRepository;
  private final StudyModuleRepository studyModuleRepository;
  private final ScheduleRepository scheduleRepository;

  public DegreeProgramServiceImpl(
      DegreeProgramRepository degreeProgramRepository,
      StudyModuleRepository studyModuleRepository,
      ScheduleRepository scheduleRepository) {
    this.degreeProgramRepository = degreeProgramRepository;
    this.studyModuleRepository = studyModuleRepository;
    this.scheduleRepository = scheduleRepository;
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
    studyModuleRepository.deleteAllStudyModulesByDegreeProgramId(degreeProgramId);
    scheduleRepository.deleteSchedule(degreeProgramId);
    degreeProgramRepository.deleteDegreeProgram(degreeProgramId);
  }
}
