package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;

import ch.ost.mas_se.oop.studyplanner.model.Semester;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import ch.ost.mas_se.oop.studyplanner.ui.views.ScheduleView;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleKey;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleView;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class ScheduleServiceImpl implements ScheduleService {

    public ScheduleServiceImpl(StudyModuleService studyModuleService,
                               DegreeProgramService degreeProgramService){
        this.studyModuleService = studyModuleService;
        this.degreeProgramService = degreeProgramService;
    };

    private DegreeProgramService degreeProgramService;
    private StudyModuleService studyModuleService;
    private List<Schedule> schedules = new ArrayList<>();

    private int sumEcts(List<StudyModule> modules_this_semester) {
        int total_ects = 0;
        for (StudyModule module : modules_this_semester){
            total_ects += module.getEcts();
        }
        return total_ects;
    }

    public Schedule calculate(String degreeProgramId){

        // 1. Schedule mit Degree Program kombinieren
        DegreeProgram associated_program = degreeProgramService.findById(degreeProgramId)
                .orElseThrow(() -> new RuntimeException("DegreeProgram not found: " + degreeProgramId));
        associated_program.printDegreeProgram();

        // 2.Schedule Gerüst erstellen
        var schedule = new Schedule(6, associated_program); // per Default 6 Semester
        int total_semesters = schedule.getTotalSemesters();

        // 3. Alle Modules from Degree Program holen
        List<StudyModule> all_modules = studyModuleService.findAll(degreeProgramId);

        // 4. Iteriere durch alle Module: Solange es noch Module hat und wir noch nicht am Ende aller Semester sind
        int semester_counter = 1;
        List<StudyModule> scheduledModules = new ArrayList<>();

        while (semester_counter <= total_semesters && !all_modules.isEmpty()) {
            List<StudyModule> modules_this_semester = new ArrayList<>();
            List<String> modules_this_semester_keys = new ArrayList<>();

            for (StudyModule module : all_modules){ // durch alle vorhanden Module gehen
                List<String> PrereqsOfModule = new ArrayList<String>(module.getPrerequisiteKeys()); // Prerequisites für das Modul holen

                for (StudyModule scheduledModule : scheduledModules) { //  durch alle geplanten Module gehen
                    String scheduledKey = scheduledModule.getStudyModuleKey();
                    if (PrereqsOfModule.contains(scheduledKey)) {
                        PrereqsOfModule.remove(scheduledKey); // Lösche jeden Prereq aus der Liste des jeweilgen Moduls, der bereits eingeplant ist
                    }
                }

                if (PrereqsOfModule.isEmpty()) {
                    modules_this_semester.add(module); //Wenn jetzt nur noch Module übrig sind, wo es keine mehr Prerequesites hat, können wir sie in diesem Semester hinzufügen
                    modules_this_semester_keys.add(module.getStudyModuleKey());
                }
            }

            // 5. Semester erstellen und Semester zum Scheduler hinzufügen
            int totalEcts = sumEcts(modules_this_semester);
            var semester = new Semester(semester_counter, totalEcts, modules_this_semester, modules_this_semester_keys, schedule);
            schedule.addSemester(semester);

            // 6. Liste: Module des Semesters (modules_this_semester) zu Scheduled Modules hinzufügen
            scheduledModules.addAll(modules_this_semester);

            // 7. Liste: Aller vorhanden Module kürzen, mit denen die eingeplant wurden
            all_modules.removeAll(modules_this_semester);

            semester_counter++;
        }
        // 8. Schedule Timestamp setzen nachdem alles berechnet
        schedule.setCalculatedAt(LocalDateTime.now());
        schedule.printSchedule();

        // 9. Alten Schedule für dieses Programm entfernen falls vorhanden
        schedules.removeIf(s -> degreeProgramId.equals(s.getDegreeProgramID()));
        schedules.add(schedule); // neuen Schedule hinzufügen
        return schedule;
        }

    public Optional<Schedule> findById(String degreeProgramId){
        for (Schedule entry : schedules) {
            if (degreeProgramId.equals(entry.getDegreeProgramID())) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();

    }

    // Methoden für Views
    private ScheduleView toView(Schedule schedule) {
        return new ScheduleView(
            schedule.getCalculatedAt(),
            schedule.getTotalSemesters()
        );
    }

    @Override
    public Optional<ScheduleView> findViewByID(String degreeProgramId) {
        Optional<Schedule> schedule = findById(degreeProgramId);
        if (schedule.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toView(schedule.get()));
    }

    }

