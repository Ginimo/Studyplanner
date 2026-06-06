package ch.ost.mas_se.oop.studyplanner.ui.views;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;

import java.util.List;

public class SemesterView {

    private final int semesterNumber;
    private final List<StudyModule> studyModules;
    private final int totalEcts;
    private final List<String> studyModuleKeys;

    public SemesterView(final int semesterNumber, final List<StudyModule> studyModules,
                        final int totalEcts, final List<String> studyModuleKeys) {
        this.semesterNumber = semesterNumber;
        this.studyModules = studyModules;
        this.totalEcts = totalEcts;
        this.studyModuleKeys = studyModuleKeys;
    }

    public int getSemesterNumber() { return semesterNumber; }
    public List<StudyModule> getStudyModules() { return studyModules; }
    public int getTotalEcts() { return totalEcts; }
    public List<String> getStudyModuleKeys() { return studyModuleKeys; }
}
