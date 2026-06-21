package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import java.util.List;
import java.util.Optional;

public interface DegreeProgramService {

  /** Persists a new degree program. */
  void create(DegreeProgram program);

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

  /** Deletes the degree program with the given ID. */
  void delete(String id);
}
