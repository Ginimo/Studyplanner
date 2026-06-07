package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.Schedule;

import ch.ost.mas_se.oop.studyplanner.model.Semester;
import ch.ost.mas_se.oop.studyplanner.ui.views.SemesterView;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SemesterServiceImpl implements SemesterService {

    private final ScheduleService scheduleService;

    public SemesterServiceImpl(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public List<Semester> findAll(String degreeProgramId) {
        Optional<Schedule> schedule = scheduleService.findById(degreeProgramId);
        if (schedule.isEmpty()) {
            return new ArrayList<>();
        }
        return schedule.get().getSemesters();
    }

    @Override
    public List<SemesterView> findViewAll(String degreeProgramId) {
        List<Semester> semesters = findAll(degreeProgramId);
        return toViewList(semesters);
    }

    private List<SemesterView> toViewList(List<Semester> semester_list) {
        List<SemesterView> views = new ArrayList<>();
        for (Semester semester : semester_list) {
            views.add(new SemesterView(
                    semester.getSemesterNumber(),
                    semester.getStudyModulesSemester(),
                    semester.getTotalEcts(),
                    semester.getStudyModuleKeys()
            ));
        }
        return views;
    }
}