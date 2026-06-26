package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudyModuleService {

  /** Constructs and persists a new study module from the given field values. */
  void createStudyModule(
      String key,
      String degreeProgramId,
      String name,
      String description,
      int ects,
      List<String> prerequisiteKeys,
      LocalDate offeredFrom,
      LocalDate offeredTo);

  /**
   * Returns the study module identified by the composite key ({@code degreeProgramId}, {@code
   * key}), or {@link java.util.Optional#empty()} if none exists.
   */
  Optional<StudyModule> findStudyModuleByKey(String degreeProgramId, String key);

  /** Returns all study modules belonging to the given degree program. */
  List<StudyModule> findAllStudyModulesByDegreeProgramId(String degreeProgramId);

  /**
   * Returns all study modules in {@code degreeProgramId} whose key appears in {@code keys}. Loads
   * the program's modules once and filters by a set lookup; unknown keys are silently skipped.
   */
  List<StudyModule> findStudyModulesByKeys(String degreeProgramId, List<String> keys);

  /**
   * Constructs a replacement and persists it, matching the stored module by its composite key
   * ({@code key}, {@code degreeProgramId}).
   */
  void updateStudyModule(
      String key,
      String degreeProgramId,
      String name,
      String description,
      int ects,
      List<String> prerequisiteKeys,
      LocalDate offeredFrom,
      LocalDate offeredTo);

  /**
   * Deletes the study module identified by ({@code degreeProgramId}, {@code key}) and removes its
   * key from the prerequisite lists of any sibling modules in the same program to avoid dangling
   * references.
   */
  void deleteStudyModule(String degreeProgramId, String key);
}
