package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;

import java.util.List;
import java.util.Optional;

public interface StudyModuleService {

    void create(StudyModule module);

    Optional<StudyModule> findByKey(String degreeProgramId, String key);

    List<StudyModule> findAllByDegreeProgramId(String degreeProgramId);

    void update(StudyModule module);

    void delete(String degreeProgramId, String key);
}
