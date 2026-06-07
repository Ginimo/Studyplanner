package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.ui.forms.DegreeProgramForm;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;

import java.util.List;
import java.util.Optional;

public interface DegreeProgramService {

    void create(DegreeProgramForm form);

    Optional<DegreeProgram> findById(String id);
    List<DegreeProgram> findAll();

    Optional<DegreeProgramView> findViewById(String id);

    void update(DegreeProgramForm form, String id);
    void delete(String id);


}
