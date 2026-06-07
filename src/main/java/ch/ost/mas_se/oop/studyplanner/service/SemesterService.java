package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.Semester;
import ch.ost.mas_se.oop.studyplanner.ui.views.SemesterView;

import java.util.List;

public interface SemesterService {

    List<Semester> findAll(String degreeProgramId);

    List<SemesterView> findViewAll(String degreeProgramId);

}
