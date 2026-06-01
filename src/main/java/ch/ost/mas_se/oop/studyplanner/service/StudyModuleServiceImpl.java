package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.ui.forms.StudyModuleForm;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;

import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleKey;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleView;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudyModuleServiceImpl implements StudyModuleService {

    private DegreeProgramService degreeProgramService;
    private List<StudyModule> modules = new ArrayList<StudyModule>();  //to decide: ArrayList, LinkList, Set oder Map

    private StudyModuleView view;
    private List<StudyModuleView> views = new ArrayList<StudyModuleView>(); ;


    public StudyModuleServiceImpl(DegreeProgramService degreeProgramService) {
        this.degreeProgramService = degreeProgramService;
    }

    @PostConstruct
    public void init() {
        DegreeProgram program = degreeProgramService.findById("sample-id-1234").orElse(null);
        DegreeProgram program_2 = degreeProgramService.findById("sample-id-1235").orElse(null);

        // Degree Program: Informatik
        var MODULE_OO = new StudyModule(
                "OO",
                "Object-Oriented Programming",
                "Foundations of object-oriented design and programming.",
                4,
                List.of(),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2029, 5, 31),
                program
        );
        modules.add(MODULE_OO);

        var MODULE_MATH = new StudyModule(
                "Math",
                "Mathematics",
                "Mathematical foundations for computer science.",
                4,
                List.of(),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2029, 5, 31),
                program
        );
        modules.add(MODULE_MATH);

        var MODULE_DB1 = new StudyModule(
                "DB1",
                "Databases 1",
                "Introduction to relational databases and SQL.",
                4,
                List.of("OO"),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2029, 5, 31),
                program
        );
        modules.add(MODULE_DB1);

        var MODULE_AD1 = new StudyModule(
                "AD1",
                "Algorithms and Data Structures 1",
                "Fundamental algorithms and data structures.",
                4,
                List.of("OO"),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2029, 5, 31),
                program
        );
        modules.add(MODULE_AD1);

        var MODULE_AD2 = new StudyModule(
                "AD2",
                "Algorithms and Data Structures 2",
                "Advanced algorithms and data structures.",
                4,
                List.of("OO", "AD1"),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2029, 5, 31),
                program
        );
        modules.add(MODULE_AD2);

        var MODULE_CPI = new StudyModule(
                "CPI",
                "Computer Programming and Interfaces",
                "Advanced programming concepts and interfaces.",
                4,
                List.of("OO", "Math"),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2029, 5, 31),
                program
        );
        modules.add(MODULE_CPI);

        var MODULE_DB2 = new StudyModule(
                "DB2",
                "Databases 2",
                "Advanced database concepts and NoSQL.",
                4,
                List.of("DB1"),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2029, 5, 31),
                program
        );
        modules.add(MODULE_DB2);

        var MODULE_SE1 = new StudyModule(
                "SE1",
                "Software Engineering 1",
                "Software development processes and practices.",
                4,
                List.of("AD1", "CPI", "DB1"),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2029, 5, 31),
                program
        );
        modules.add(MODULE_SE1);

        var MODULE_UI1 = new StudyModule(
                "UI1",
                "User Interfaces 1",
                "Introduction to UI design and implementation.",
                4,
                List.of("AD1"),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2029, 5, 31),
                program
        );
        modules.add(MODULE_UI1);

        var MODULE_SE2 = new StudyModule(
                "SE2",
                "Software Engineering 2",
                "Advanced software engineering and architecture.",
                4,
                List.of("DB1", "SE1", "UI1"),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2029, 5, 31),
                program
        );
        modules.add(MODULE_SE2);

        var MODULE_UI2 = new StudyModule(
                "UI2",
                "User Interfaces 2",
                "Advanced UI patterns and frameworks.",
                4,
                List.of("UI1"),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2029, 5, 31),
                program
        );
        modules.add(MODULE_UI2);

        var MODULE_THESIS = new StudyModule(
                "Thesis",
                "Bachelor Thesis",
                "Independent research project and thesis.",
                12,
                List.of("DB2", "SE2", "UI2"),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2029, 5, 31),
                program
        );
        modules.add(MODULE_THESIS);

        // Degree Program: Soziale Arbeit
        var MODULE_HIS = new StudyModule(
                "HIS",
                "Geschichte der Sozialen Arbeit",
                "Geschichte der Sozialen Arbeit als organisierte Hilfe.",
                4,
                List.of(),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2029, 5, 31),
                program_2
        );
        modules.add(MODULE_HIS);

    };

    // Methoden für Modules

    @Override
    public void create(StudyModuleForm form, String programId){
        DegreeProgram program = degreeProgramService.findById(programId).orElse(null);

        var module = new StudyModule(
            form.getStudyModuleKey(),
            form.getName(),
            form.getDescription(),
            form.getEcts(),
            form.getPrerequisiteKeys(),
            form.getOfferedFrom(),
            form.getOfferedTo(),
            program
        );
        module.printStudyModule();
        modules.add(module);
    }

    @Override
    public List<StudyModule> findAll(String programId) {
        List<StudyModule> result = new ArrayList<>();
        for (StudyModule entry : modules) {
            if (programId.equals(entry.getDegreeProgram().getId())) {
                result.add(entry);
                //IO.println(entry);
            }
        }

        return result;
    }

    @Override
    public Optional<StudyModule> findByKey(String studyModuleKey){
            for (StudyModule entry : modules) {
                if (studyModuleKey.equals(entry.getStudyModuleKey())) {
                    return Optional.of(entry);
                }
            }
            return Optional.empty();
    }

    // Methoden für Views

    private StudyModuleView toView(StudyModule module) {
        return new StudyModuleView(
                new StudyModuleKey(module.getStudyModuleKey()),
                module.getName(),
                module.getDescription(),
                module.getEcts(),
                module.getPrerequisiteKeys()
                );
    }

    private List<StudyModuleView> toViewList(List<StudyModule> module_list) {
        List<StudyModuleView> views = new ArrayList<>();
        for (StudyModule entry : module_list) {
            views.add(new StudyModuleView(
                    new StudyModuleKey(entry.getStudyModuleKey()),
                    entry.getName(),
                    entry.getDescription(),
                    entry.getEcts(),
                    entry.getPrerequisiteKeys()
            ));
        }
        return views;
    }

    public Optional<StudyModuleView> findViewByKey(String studyModuleKey) {
        Optional<StudyModule> module = findByKey(studyModuleKey);
        if (module.isEmpty()) {
           return Optional.empty();
        }
        return Optional.of(toView(module.get()));
    }

    @Override
    public List<StudyModuleView> findViewAll(String programId) {
        List<StudyModule> result = new ArrayList<>();
        for (StudyModule entry : modules) {
            if (programId.equals(entry.getDegreeProgram().getId())) {
                result.add(entry);
                //IO.println(entry);
            }
        }
        List<StudyModuleView> views = toViewList(result);
        return views;
    }

}
