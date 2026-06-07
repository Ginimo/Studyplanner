package ch.ost.mas_se.oop.studyplanner.service;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.ui.forms.StudyModuleForm;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleView;

import java.util.List;
import java.util.Optional;

public interface StudyModuleService {

    void create(StudyModuleForm form,  String programId);
    Optional<StudyModule> findByKey(String studyModuleKey);
    List<StudyModule> findAll(String program_id);

    Optional<StudyModuleView> findViewByKey(String studyModuleKey);
    List<StudyModuleView> findViewAll(String program_id);

    void update(StudyModuleForm form, String studyModuleKey);
    void delete(String studyModuleKey);

}
