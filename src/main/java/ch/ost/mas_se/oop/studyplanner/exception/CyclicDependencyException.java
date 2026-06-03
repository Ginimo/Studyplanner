package ch.ost.mas_se.oop.studyplanner.exception;

import java.util.List;

public class CyclicDependencyException extends RuntimeException {

    public CyclicDependencyException(List<String> remainingModuleKeys) {
        super("Cyclic prerequisite dependency detected! Cannot compute a valid study plan. "
              + "Remaining modules: " + String.join(", ", remainingModuleKeys));
    }
}
