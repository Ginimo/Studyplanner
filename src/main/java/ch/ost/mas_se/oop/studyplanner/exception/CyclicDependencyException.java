package ch.ost.mas_se.oop.studyplanner.exception;

import java.util.List;

public class CyclicDependencyException extends RuntimeException {

  /**
   * @param remainingStudyModuleKeys keys of the modules involved in or blocked by the cycle,
   *     included in the error message to aid diagnosis
   */
  public CyclicDependencyException(List<String> remainingStudyModuleKeys) {
    super(
        "Cyclic prerequisite dependency detected! Cannot compute a valid study plan. "
            + "Remaining modules: "
            + String.join(", ", remainingStudyModuleKeys));
  }
}
