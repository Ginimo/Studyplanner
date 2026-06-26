package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import java.util.List;
import java.util.Optional;

public interface StudyModuleRepository {

  /**
   * Persists the given study module, creating it if new or replacing the existing one identified by
   * its degree program ID and module key.
   */
  void saveStudyModule(StudyModule module);

  /**
   * Returns the study module identified by the composite key ({@code degreeProgramId}, {@code
   * key}), or {@link java.util.Optional#empty()} if none exists.
   */
  Optional<StudyModule> findStudyModuleByKey(String degreeProgramId, String key);

  /** Returns all study modules belonging to the given degree program in insertion order. */
  List<StudyModule> findAllStudyModulesByDegreeProgramId(String degreeProgramId);

  /**
   * Removes the study module identified by the composite key ({@code degreeProgramId}, {@code
   * key}). No-op if it does not exist.
   */
  void deleteStudyModule(String degreeProgramId, String key);
}
