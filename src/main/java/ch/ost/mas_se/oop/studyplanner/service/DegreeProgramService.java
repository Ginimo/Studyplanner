package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import java.util.List;
import java.util.Optional;

public interface DegreeProgramService {

  /**
   * Creates a new degree program with a generated ID and persists it.
   *
   * @return the newly created {@link DegreeProgram} including its generated ID
   */
  DegreeProgram create(String name, String description, int maxDurationInSemesters);

  /**
   * Returns the degree program with the given ID, or {@link java.util.Optional#empty()} if none
   * exists.
   */
  Optional<DegreeProgram> findById(String id);

  /** Returns all degree programs. */
  List<DegreeProgram> findAll();

  /**
   * Replaces the stored degree program matching {@code program}'s ID with the given updated
   * version.
   */
  void update(DegreeProgram program);

  /**
   * Deletes the degree program with the given ID, together with all of its study modules and its
   * persisted schedule.
   */
  void delete(String id);
}
