package ch.ost.mas_se.oop.studyplanner.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class DomainEqualsTest {

  @Test
  void degreeProgram_equalsAndHashCode_satisfiesContract() {
    EqualsVerifier.forClass(DegreeProgram.class)
        .withOnlyTheseFields("degreeProgramId")
        .usingGetClass()
        .verify();
  }

  @Test
  void studyModule_equalsAndHashCode_satisfiesContract() {
    EqualsVerifier.forClass(StudyModule.class)
        .withOnlyTheseFields("studyModuleKey", "degreeProgramId")
        .usingGetClass()
        .verify();
  }
}
