package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import java.util.List;
import java.util.Optional;

public interface StudyModuleService {

  /** Persists a new study module. */
  void createStudyModule(StudyModule module);

  /**
   * Returns the study module identified by the composite key ({@code degreeProgramId}, {@code
   * key}), or {@link java.util.Optional#empty()} if none exists.
   */
  Optional<StudyModule> findStudyModuleByKey(String degreeProgramId, String key);

  /** Returns all study modules belonging to the given degree program. */
  List<StudyModule> findAllStudyModulesByDegreeProgramId(String degreeProgramId);

  /**
   * Replaces the stored study module matching {@code module}'s composite key with the given updated
   * version.
   */
  void updateStudyModule(StudyModule module);

  /**
   * Deletes the study module identified by ({@code degreeProgramId}, {@code key}) and removes its
   * key from the prerequisite lists of any sibling modules in the same program to avoid dangling
   * references.
   */
  void deleteStudyModule(String degreeProgramId, String key);
}
