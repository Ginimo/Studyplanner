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
  public DegreeProgram create(String name, String description, int maxDurationInSemesters) {
    DegreeProgram program =
        new DegreeProgram(UUID.randomUUID().toString(), name, description, maxDurationInSemesters);
    repository.save(program);
    return program;
  }

  @Override
  public Optional<DegreeProgram> findById(String id) {
    return repository.findById(id);
  }

  @Override
  public List<DegreeProgram> findAll() {
    return repository.findAll();
  }

  @Override
  public void update(DegreeProgram program) {
    repository.save(program);
  }

  @Override
  public void delete(String id) {
    studyModuleService
        .findAllByDegreeProgramId(id)
        .forEach(m -> studyModuleService.delete(id, m.getStudyModuleKey()));
    scheduleService.deleteByDegreeProgramId(id);
    repository.delete(id);
  }
}
