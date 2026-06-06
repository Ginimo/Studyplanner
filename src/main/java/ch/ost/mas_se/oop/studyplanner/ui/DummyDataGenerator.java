package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import ch.ost.mas_se.oop.studyplanner.ui.views.ScheduleView;
import ch.ost.mas_se.oop.studyplanner.ui.views.SemesterView;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleKey;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleListView;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleView;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class DummyDataGenerator {

    // TODO: this ugly class needs to be removed by the end of the project ;)

    private static final String PROGRAM_ID = "sample-id-1234";

    private static final StudyModuleView MODULE_OO = new StudyModuleView(
            new StudyModuleKey("OO"), "Object-Oriented Programming",
            "Foundations of object-oriented design and programming.", 4, List.of());

    private static final StudyModuleView MODULE_MATH = new StudyModuleView(
            new StudyModuleKey("Math"), "Mathematics",
            "Mathematical foundations for computer science.", 4, List.of());

    private static final StudyModuleView MODULE_DB1 = new StudyModuleView(
            new StudyModuleKey("DB1"), "Databases 1",
            "Introduction to relational databases and SQL.", 4, List.of("OO"));

    private static final StudyModuleView MODULE_AD1 = new StudyModuleView(
            new StudyModuleKey("AD1"), "Algorithms and Data Structures 1",
            "Fundamental algorithms and data structures.", 4, List.of("OO"));

    private static final StudyModuleView MODULE_AD2 = new StudyModuleView(
            new StudyModuleKey("AD2"), "Algorithms and Data Structures 2",
            "Advanced algorithms and data structures.", 4, List.of("OO", "AD1"));

    private static final StudyModuleView MODULE_CPI = new StudyModuleView(
            new StudyModuleKey("CPI"), "Computer Programming and Interfaces",
            "Advanced programming concepts and interfaces.", 4, List.of("OO", "Math"));

    private static final StudyModuleView MODULE_DB2 = new StudyModuleView(
            new StudyModuleKey("DB2"), "Databases 2",
            "Advanced database concepts and NoSQL.", 4, List.of("DB1"));

    private static final StudyModuleView MODULE_SE1 = new StudyModuleView(
            new StudyModuleKey("SE1"), "Software Engineering 1",
            "Software development processes and practices.", 4, List.of("AD1", "CPI", "DB1"));

    private static final StudyModuleView MODULE_UI1 = new StudyModuleView(
            new StudyModuleKey("UI1"), "User Interfaces 1",
            "Introduction to UI design and implementation.", 4, List.of("AD1"));

    private static final StudyModuleView MODULE_SE2 = new StudyModuleView(
            new StudyModuleKey("SE2"), "Software Engineering 2",
            "Advanced software engineering and architecture.", 4, List.of("DB1", "SE1", "UI1"));

    private static final StudyModuleView MODULE_A1 = new StudyModuleView(
            new StudyModuleKey("SE2"), "Geschichte der Sozialen Arbeit",
            "Geschichte der Sozialen Arbeit als organisierte Hilfe", 6, List.of());

    private static final StudyModuleView MODULE_UI2 = new StudyModuleView(
            new StudyModuleKey("UI2"), "User Interfaces 2",
            "Advanced UI patterns and frameworks.", 4, List.of("UI1"));

    private static final StudyModuleView MODULE_THESIS = new StudyModuleView(
            new StudyModuleKey("Thesis"), "Bachelor Thesis",
            "Independent research project and thesis.", 12, List.of("DB2", "SE2", "UI2"));

    private static final List<StudyModuleView> STUDY_MODULE_VIEWS = List.of(
            MODULE_OO, MODULE_MATH, MODULE_DB1, MODULE_AD1, MODULE_AD2, MODULE_CPI,
            MODULE_DB2, MODULE_SE1, MODULE_UI1, MODULE_SE2, MODULE_UI2, MODULE_THESIS);

    private static final List<StudyModuleListView> STUDY_MODULE_LIST_VIEWS = List.of(
            new StudyModuleListView(new StudyModuleKey("OO"), "Object-Oriented Programming",
                    4, LocalDate.of(2024, 9, 1), null, false, 0),
            new StudyModuleListView(new StudyModuleKey("Math"), "Mathematics",
                    4, LocalDate.of(2024, 9, 1), null, false, 0),
            new StudyModuleListView(new StudyModuleKey("DB1"), "Databases 1",
                    4, LocalDate.of(2024, 9, 1), null, true, 1),
            new StudyModuleListView(new StudyModuleKey("AD1"), "Algorithms and Data Structures 1",
                    4, LocalDate.of(2024, 9, 1), null, true, 1),
            new StudyModuleListView(new StudyModuleKey("AD2"), "Algorithms and Data Structures 2",
                    4, LocalDate.of(2024, 9, 1), null, true, 1),
            new StudyModuleListView(new StudyModuleKey("CPI"), "Computer Programming and Interfaces",
                    4, LocalDate.of(2024, 9, 1), null, true, 2),
            new StudyModuleListView(new StudyModuleKey("DB2"), "Databases 2",
                    4, LocalDate.of(2024, 9, 1), null, true, 1),
            new StudyModuleListView(new StudyModuleKey("SE1"), "Software Engineering 1",
                    4, LocalDate.of(2024, 9, 1), null, true, 3),
            new StudyModuleListView(new StudyModuleKey("UI1"), "User Interfaces 1",
                    4, LocalDate.of(2024, 9, 1), null, true, 1),
            new StudyModuleListView(new StudyModuleKey("SE2"), "Software Engineering 2",
                    4, LocalDate.of(2024, 9, 1), null, true, 3),
            new StudyModuleListView(new StudyModuleKey("UI2"), "User Interfaces 2",
                    4, LocalDate.of(2024, 9, 1), null, true, 1),
            new StudyModuleListView(new StudyModuleKey("Thesis"), "Bachelor Thesis",
                    12, LocalDate.of(2024, 9, 1), null, true, 3)
    );

    private static final ScheduleView SCHEDULE_VIEW = new ScheduleView(
            LocalDateTime.of(2024, 10, 1, 10, 0), 5);


    public List<DegreeProgramView> getListOfDegreePrograms() {
        return List.of(
                new DegreeProgramView(
                        PROGRAM_ID,
                        "BSc Informatik",
                        "Studiengang Informatik der OST",
                        12),
                new DegreeProgramView(
                        PROGRAM_ID,
                        "BSc Soziale Arbeit",
                        "Studiengang Soziale Arbeit der OST",
                        12)
        );
    }

    public Optional<DegreeProgramView> getDegreeProgramById(final String id) {
        if (PROGRAM_ID.equals(id)) {
            // TODO: assumtion list always contains at least 1 element, best practise to check if list size() > 0
            return Optional.of(getListOfDegreePrograms().getFirst());
        }
        return Optional.empty();
    }

    public List<StudyModuleListView> getStudyModuleListViewEntriesByProgramId(final String degreeProgramId) {
        if (PROGRAM_ID.equals(degreeProgramId)) {
            return STUDY_MODULE_LIST_VIEWS;
        }
        return List.of();
    }

    public List<StudyModuleView> getStudyModuleViewByProgramId(final String degreeProgramId) {
        if (PROGRAM_ID.equals(degreeProgramId)) {
            return STUDY_MODULE_VIEWS;
        }
        return List.of();
    }

    public Optional<StudyModuleView> getStudyModuleViewByKey(final String key) {
        return STUDY_MODULE_VIEWS.stream()
                .filter(sm -> sm.getStudyModuleKey().key().equals(key))
                .findFirst();
    }

    public Optional<ScheduleView> getScheduleViewForProgram(final String id) {
        if (PROGRAM_ID.equals(id)) {
            return Optional.of(SCHEDULE_VIEW);
        }
        return Optional.empty();
    }

}
