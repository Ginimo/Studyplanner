package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JsonStudyModuleRepository implements StudyModuleRepository {


  @Override
  public void save(StudyModule module) {

  }

  @Override
  public Optional<StudyModule> findByKey(String degreeProgramId, String key) {
    return Optional.empty();
  }

  @Override
  public List<StudyModule> findAllByDegreeProgramId(String degreeProgramId) {
    return List.of();
  }

  @Override
  public void delete(String degreeProgramId, String key) {

  }
}
