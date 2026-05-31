package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.service.StudyModuleService;

import ch.ost.mas_se.oop.studyplanner.model.Semester;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    public ScheduleServiceImpl(StudyModuleService studyModuleService){
        this.studyModuleService = studyModuleService;
    };

    private final StudyModuleService studyModuleService;

    //public Schedule calculate(int degreeProgramId){

        // 1. Find all Modules from the Program
      //  List<StudyModule> assigned_modules = studyModuleService.findAll(degreeProgramId);


        //2. Find all Modules without Prerequisites
        // var Semester = new Semester()

        //3. Save them to the Semester 1
        // var Semester = new Semester()

        //4. Delete modules from Semester 1
        // var Semester = new Semester()

        //5. Go further with the rest semesters
        // var Semester = new Semester()

      //  return Schedule
   // }
    //List<StudyModule> sortByPrerequisites(List<StudyModule> modules);


}
