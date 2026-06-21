package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;

public interface ScheduleService {

  /**
   * Computes a semester-by-semester study schedule for the given degree program by topologically
   * sorting its modules according to their prerequisite dependencies.
   *
   * @param program the degree program whose modules are to be scheduled
   * @return a {@link Schedule} where each semester contains the modules whose prerequisites were
   *     all satisfied in earlier semesters
   * @throws ch.ost.mas_se.oop.studyplanner.exception.CyclicDependencyException if the prerequisite
   *     graph contains a cycle
   */
  Schedule calculate(DegreeProgram program);
}
