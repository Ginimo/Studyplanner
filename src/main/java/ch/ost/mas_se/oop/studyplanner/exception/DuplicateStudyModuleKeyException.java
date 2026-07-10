package ch.ost.mas_se.oop.studyplanner.exception;

public class DuplicateStudyModuleKeyException extends RuntimeException {

  public DuplicateStudyModuleKeyException(String studyModuleKey, String degreeProgramId) {
    super(
        "Study module key \""
            + studyModuleKey
            + "\" already exists in degree program \""
            + degreeProgramId
            + "\".");
  }
}
