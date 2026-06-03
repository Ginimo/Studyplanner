package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;

import java.util.List;
import java.util.Optional;

public interface StudyModuleRepository {

    void save(StudyModule module);

    Optional<StudyModule> findByKey(String degreeProgramId, String key);

    List<StudyModule> findAllByDegreeProgramId(String degreeProgramId);

    void delete(String degreeProgramId, String key);
}
