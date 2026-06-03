package ch.ost.mas_se.oop.studyplanner.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Semester {
    private final int semesterNumber;
    private final List<StudyModule> studyModules;

    public Semester(int semesterNumber) {
        this.semesterNumber = semesterNumber;
        this.studyModules = new ArrayList<>();
    }

    public int getSemesterNumber() {
        return semesterNumber;
    }

    public List<StudyModule> getStudyModules() {
        return Collections.unmodifiableList(studyModules);
    }

    public void addStudyModule(StudyModule module) {
        this.studyModules.add(module);
    }

    public int getTotalEcts() {
        return studyModules.stream().mapToInt(StudyModule::getEcts).sum();
    }
}
