package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JsonDegreeProgramRepository implements DegreeProgramRepository {

  @Override
  public void save(DegreeProgram program) {

  }

  @Override
  public Optional<DegreeProgram> findById(String id) {
    return Optional.empty();
  }

  @Override
  public List<DegreeProgram> findAll() {
    return List.of();
  }

  @Override
  public void delete(String id) {

  }
}
