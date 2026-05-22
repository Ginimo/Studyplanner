package ch.ost.mas_se.oop.studyplanner.ui.views;

public record StudyModuleKey(String key) {

    @Override
    public String toString() {
        return key;
    }
}
