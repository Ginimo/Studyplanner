package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

public interface DegreeProgramService {

  /**
   * Creates a new degree program with a generated ID and persists it.
   *
   * @return the newly created {@link DegreeProgram} including its generated ID
   */
  DegreeProgram createDegreeProgram(
      @NotBlank String name, String description, @Min(1) int maxDurationInSemesters);

  /**
   * Returns the degree program with the given ID, or {@link java.util.Optional#empty()} if none
   * exists.
   */
  Optional<DegreeProgram> findDegreeProgramById(String degreeProgramId);

  /** Returns all degree programs. */
  List<DegreeProgram> findAllDegreePrograms();

  /** Replaces the stored degree program matching {@code degreeProgramId} with new field values. */
  void updateDegreeProgram(
      String degreeProgramId,
      @NotBlank String name,
      String description,
      @Min(1) int maxDurationInSemesters);

  /**
   * Deletes the degree program with the given ID, together with all of its study modules and its
   * persisted schedule.
   */
  void deleteDegreeProgram(String degreeProgramId);
}
