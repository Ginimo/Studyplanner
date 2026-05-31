package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.ui.forms.StudyModuleForm;
import ch.ost.mas_se.oop.studyplanner.repository.StudyModuleRepository;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleView;

import java.util.List;
import java.util.Optional;

public interface StudyModuleService {

    void create(StudyModuleForm form,  String programId);
    Optional<StudyModule> findByKey(String studyModuleKey);
    List<StudyModule> findAll(String program_id);
    Optional<StudyModuleView> findViewById(String studyModuleKey);
    //void update(StudyModule module); // ToDo
    //void delete(String studyModuleKey); // ToDo

}
