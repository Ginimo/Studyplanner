package ch.ost.mas_se.oop.studyplanner.service;
import ch.ost.mas_se.oop.studyplanner.model.Semester;
import ch.ost.mas_se.oop.studyplanner.ui.views.ScheduleView;
import ch.ost.mas_se.oop.studyplanner.ui.views.SemesterView;

import java.util.List;
import java.util.Optional;

public interface SemesterService {

    List<Semester> findAll(String degreeProgramId);

    List<SemesterView> findViewAll(String degreeProgramId);

}
