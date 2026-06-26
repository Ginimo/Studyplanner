package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.repository.DegreeProgramRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DegreeProgramServiceImpl implements DegreeProgramService {
  private final DegreeProgramRepository repository;
  private final StudyModuleService studyModuleService;
  private final ScheduleService scheduleService;

  public DegreeProgramServiceImpl(
      DegreeProgramRepository repository,
      StudyModuleService studyModuleService,
      ScheduleService scheduleService) {
    this.repository = repository;
    this.studyModuleService = studyModuleService;
    this.scheduleService = scheduleService;
  }

  @Override
  public DegreeProgram createDegreeProgram(
      String name, String description, int maxDurationInSemesters) {
    DegreeProgram program =
        new DegreeProgram(UUID.randomUUID().toString(), name, description, maxDurationInSemesters);
    repository.saveDegreeProgram(program);
    return program;
  }

  @Override
  public Optional<DegreeProgram> findDegreeProgramById(String id) {
    return repository.findByDegreeProgramId(id);
  }

  @Override
  public List<DegreeProgram> findAllDegreePrograms() {
    return repository.findAllDegreePrograms();
  }

  @Override
  public void updateDegreeProgram(DegreeProgram program) {
    repository.saveDegreeProgram(program);
  }

  @Override
  public void deleteDegreeProgram(String id) {
    studyModuleService
        .findAllStudyModulesByDegreeProgramId(id)
        .forEach(m -> studyModuleService.deleteStudyModule(id, m.getStudyModuleKey()));
    scheduleService.deleteSchedule(id);
    repository.deleteDegreeProgram(id);
  }
}
