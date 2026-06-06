package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.ui.forms.DegreeProgramForm;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import ch.ost.mas_se.oop.studyplanner.repository.DegreeProgramRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DegreeProgramServiceImpl implements DegreeProgramService {

    private List<DegreeProgram> programs = new ArrayList<>(); //to decide: ArrayList, LinkList, Set oder Map
    private DegreeProgramView view;

    public DegreeProgramServiceImpl(){}


    @PostConstruct
    public void init() {
        // Nur vorläufig: Damit initial nach dem Start der Applikation schon Daten vorhanden sind
        var hard_coded_program = new DegreeProgram(
                "sample-id-1234",
                "BSc Informatik",
                "Studiengang Informatik der OST",
                12
        );
        programs.add(hard_coded_program);
        var hard_coded_program_2 = new DegreeProgram(
                "sample-id-1235",
                "BSc Soziale Arbeit",
                "Studiengang Soziale Arbeit der OST",
                12
        );
        programs.add(hard_coded_program_2);
    };

    // Methoden für Model

    @Override
    public void create(DegreeProgramForm form) {
                var program = new DegreeProgram(
                UUID.randomUUID().toString(), // ← zufällige eindeutige ID
                form.getName(),
                form.getDescription(),
                form.getMaxDurationInSemesters()
        );
        program.printDegreeProgram();
        programs.add(program);
    }


    @Override
    public List<DegreeProgram>  findAll(){
        return programs;
    }

    @Override
    public Optional<DegreeProgram> findById(String id) {
        for (DegreeProgram entry : programs) {
            if (id.equals(entry.getId())) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    private DegreeProgramView toView(DegreeProgram program) {
        return new DegreeProgramView(
                program.getId(),
                program.getName(),
                program.getDescription(),
                program.getMaxDurationInSemesters()
                );
    }

    // Methoden für Views
    public Optional<DegreeProgramView> findViewById(final String id) {
        Optional<DegreeProgram> program = findById(id);
        if (program.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toView(program.get()));
    }



}


