package ch.ost.mas_se.oop.studyplanner;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.StudyModuleService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SampleDataInitializer implements ApplicationRunner {

  private final DegreeProgramService degreeProgramService;
  private final StudyModuleService studyModuleService;

  public SampleDataInitializer(
      DegreeProgramService degreeProgramService, StudyModuleService studyModuleService) {
    this.degreeProgramService = degreeProgramService;
    this.studyModuleService = studyModuleService;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (!degreeProgramService.findAllDegreePrograms().isEmpty()) {
      return;
    }

    LocalDate offeredFrom = LocalDate.of(2026, 6, 1);
    LocalDate offeredTo = LocalDate.of(2030, 12, 31);

    DegreeProgram bscInfo =
        degreeProgramService.createDegreeProgram(
            "BSc Informatik", "Studiengang Informatik der OST", 12);
    degreeProgramService.createDegreeProgram(
        "BSc Soziale Arbeit", "Studiengang Soziale Arbeit der OST", 12);
    String progId = bscInfo.getDegreeProgramId();

    studyModuleService.createStudyModule(
        "OO",
        progId,
        "Object-Oriented Programming",
        "Foundations of object-oriented design and programming.",
        4,
        List.of(),
        offeredFrom,
        offeredTo);
    studyModuleService.createStudyModule(
        "Math",
        progId,
        "Mathematics",
        "Mathematical foundations for computer science.",
        4,
        List.of(),
        offeredFrom,
        offeredTo);
    studyModuleService.createStudyModule(
        "DB1",
        progId,
        "Databases 1",
        "Introduction to relational databases and SQL.",
        4,
        List.of("OO"),
        offeredFrom,
        offeredTo);
    studyModuleService.createStudyModule(
        "AD1",
        progId,
        "Algorithms and Data Structures 1",
        "Fundamental algorithms and data structures.",
        4,
        List.of("OO"),
        offeredFrom,
        offeredTo);
    studyModuleService.createStudyModule(
        "AD2",
        progId,
        "Algorithms and Data Structures 2",
        "Advanced algorithms and data structures.",
        4,
        List.of("OO", "AD1"),
        offeredFrom,
        offeredTo);
    studyModuleService.createStudyModule(
        "CPI",
        progId,
        "Computer Programming and Interfaces",
        "Advanced programming concepts and interfaces.",
        4,
        List.of("OO", "Math"),
        offeredFrom,
        offeredTo);
    studyModuleService.createStudyModule(
        "DB2",
        progId,
        "Databases 2",
        "Advanced database concepts and NoSQL.",
        4,
        List.of("DB1"),
        offeredFrom,
        offeredTo);
    studyModuleService.createStudyModule(
        "SE1",
        progId,
        "Software Engineering 1",
        "Software development processes and practices.",
        4,
        List.of("AD1", "CPI", "DB1"),
        offeredFrom,
        offeredTo);
    studyModuleService.createStudyModule(
        "UI1",
        progId,
        "User Interfaces 1",
        "Introduction to UI design and implementation.",
        4,
        List.of("AD1"),
        offeredFrom,
        offeredTo);
    studyModuleService.createStudyModule(
        "SE2",
        progId,
        "Software Engineering 2",
        "Advanced software engineering and architecture.",
        4,
        List.of("DB1", "SE1", "UI1"),
        offeredFrom,
        offeredTo);
    studyModuleService.createStudyModule(
        "UI2",
        progId,
        "User Interfaces 2",
        "Advanced UI patterns and frameworks.",
        4,
        List.of("UI1"),
        offeredFrom,
        offeredTo);
    studyModuleService.createStudyModule(
        "Thesis",
        progId,
        "Bachelor Thesis",
        "Independent research project and thesis.",
        12,
        List.of("DB2", "SE2", "UI2"),
        offeredFrom,
        offeredTo);
  }
}
