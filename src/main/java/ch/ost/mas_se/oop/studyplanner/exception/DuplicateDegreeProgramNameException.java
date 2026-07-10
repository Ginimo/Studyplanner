package ch.ost.mas_se.oop.studyplanner.exception;

public class DuplicateDegreeProgramNameException extends RuntimeException {

  public DuplicateDegreeProgramNameException(String name) {
    super("A degree program with the name \"" + name + "\" already exists.");
  }
}
