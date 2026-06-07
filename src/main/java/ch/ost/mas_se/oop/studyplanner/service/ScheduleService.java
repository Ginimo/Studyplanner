package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.ui.views.ScheduleView;

import java.util.Optional;

public interface ScheduleService {


    Schedule calculate(String degreeProgramId);

    Optional<Schedule> findById(String degreeProgramId);

    Optional<ScheduleView> findViewByID(String degreeProgramId);

}
