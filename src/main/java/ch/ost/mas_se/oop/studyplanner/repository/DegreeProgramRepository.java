package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import java.util.List;
import java.util.Optional;

public interface DegreeProgramRepository {

  /**
   * Persists the given degree program, creating it if new or replacing the existing one with the
   * same ID.
   */
  void save(DegreeProgram program);

  /**
   * Returns the degree program with the given ID, or {@link java.util.Optional#empty()} if none
   * exists.
   */
  Optional<DegreeProgram> findById(String id);

  /** Returns all stored degree programs in insertion order. */
  List<DegreeProgram> findAll();

  /** Removes the degree program with the given ID. No-op if it does not exist. */
  void delete(String id);
}
