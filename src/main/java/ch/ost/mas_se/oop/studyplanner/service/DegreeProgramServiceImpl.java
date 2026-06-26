package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.repository.DegreeProgramRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DegreeProgramServiceImpl implements DegreeProgramService {
  private final DegreeProgramRepository repository;

  public DegreeProgramServiceImpl(DegreeProgramRepository repository) {
    this.repository = repository;
  }

  @Override
  public void create(DegreeProgram program) {
    repository.save(program);
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
    repository.delete(id);
  }
}
