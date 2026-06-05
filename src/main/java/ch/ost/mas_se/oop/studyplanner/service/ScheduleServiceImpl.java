package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.service.StudyModuleService;

import ch.ost.mas_se.oop.studyplanner.model.Semester;
import ch.ost.mas_se.oop.studyplanner.ui.forms.StudyModuleForm;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ScheduleServiceImpl implements ScheduleService {

    public ScheduleServiceImpl(StudyModuleService studyModuleService,
                               DegreeProgramService degreeProgramService){
        this.studyModuleService = studyModuleService;
        this.degreeProgramService = degreeProgramService;
    };

    private DegreeProgramService degreeProgramService;
    private StudyModuleService studyModuleService;

    private List<StudyModule> sortByPrerequisitesSemester1(List<StudyModule> modules){

        List<StudyModule> modules_sorted = new ArrayList<>();

        for (StudyModule entry : modules) {
            if (entry.hasPrerequisites()== false) {
                modules_sorted.add(entry);
            }
        }
        return modules_sorted;
    }


    public Schedule calculate(String degreeProgramId){

        // 0. Create a Schedule with the associated Degree Program
        DegreeProgram associated_program = degreeProgramService.findById(degreeProgramId)
                .orElseThrow(() -> new RuntimeException("DegreeProgram not found: " + degreeProgramId));
        var schedule = new Schedule(LocalDate.of(2026, 6, 1).atStartOfDay(), 6, associated_program);

        // 1. Find all Modules from the Program
        List<StudyModule> assigned_modules = studyModuleService.findAll(degreeProgramId);

        //2. Find all Modules without Prerequisites
        List<StudyModule>  modules_semester_1 =  sortByPrerequisitesSemester1(assigned_modules);
        for (StudyModule module: modules_semester_1){
            module.printStudyModule();
        }

        //3. Save them to the Semester 1

        //4. Delete modules from Semester 1

        //5. Go further with the rest semesters



       return schedule;
    }
    //List<StudyModule> sortByPrerequisites(List<StudyModule> modules);


}
