package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudyModuleService {

  /** Constructs and persists a new study module from the given field values. */
  void createStudyModule(
      @NotBlank String studyModuleKey,
      @NotBlank String degreeProgramId,
      @NotBlank String name,
      String description,
      @Min(1) int ects,
      List<String> prerequisiteKeys,
      LocalDate offeredFrom,
      LocalDate offeredTo);

  /**
   * Returns the study module identified by the composite key ({@code degreeProgramId}, {@code
   * studyModuleKey}), or {@link java.util.Optional#empty()} if none exists.
   */
  Optional<StudyModule> findStudyModuleByKey(String degreeProgramId, String studyModuleKey);

  /** Returns all study modules belonging to the given degree program. */
  List<StudyModule> findAllStudyModulesByDegreeProgramId(String degreeProgramId);

  /**
   * Returns all study modules in {@code degreeProgramId} whose key appears in {@code
   * studyModuleKeys}. Loads the program's modules once and filters by a set lookup; unknown keys
   * are silently skipped.
   */
  List<StudyModule> findStudyModulesByKeys(String degreeProgramId, List<String> studyModuleKeys);

  /**
   * Constructs a replacement and persists it, matching the stored module by its composite key
   * ({@code studyModuleKey}, {@code degreeProgramId}).
   */
  void updateStudyModule(
      @NotBlank String studyModuleKey,
      @NotBlank String degreeProgramId,
      @NotBlank String name,
      String description,
      @Min(1) int ects,
      List<String> prerequisiteKeys,
      LocalDate offeredFrom,
      LocalDate offeredTo);

  /**
   * Deletes the study module identified by ({@code degreeProgramId}, {@code studyModuleKey}) and
   * removes its key from the prerequisite lists of any sibling modules in the same program to avoid
   * dangling references.
   */
  void deleteStudyModule(String degreeProgramId, String studyModuleKey);

  /**
   * Deletes all study modules belonging to {@code degreeProgramId} and invalidates the associated
   * schedule. Intended for use when an entire degree program is being removed.
   */
  void deleteAllStudyModulesByDegreeProgramId(String degreeProgramId);

  /**
   * Returns all study modules for {@code degreeProgramId} that match the optional filters, sorted
   * by the given field and direction. A blank {@code nameFilter} or null {@code offeredAt} disables
   * the respective filter. {@code sortBy} accepts {@code NAME}, {@code OFFERED_FROM}, {@code
   * OFFERED_TO}, or {@code ECTS}; {@code sortDir} accepts {@code ASC} or {@code DESC}.
   */
  List<StudyModule> findFilteredStudyModules(
      String degreeProgramId,
      String nameFilter,
      LocalDate offeredAt,
      String sortBy,
      String sortDir);

  /**
   * Returns all study modules for {@code degreeProgramId} indexed by their key, for O(1) lookup
   * when resolving module keys to module details (e.g. assembling schedule views).
   */
  Map<String, StudyModule> findModuleMapByDegreeProgramId(String degreeProgramId);
}
