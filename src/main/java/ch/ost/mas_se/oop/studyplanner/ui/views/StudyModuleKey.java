package ch.ost.mas_se.oop.studyplanner.ui.views;

import java.util.Objects;

public class StudyModuleKey {

  private final String key;

  public StudyModuleKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    StudyModuleKey that = (StudyModuleKey) other;
    return Objects.equals(key, that.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key);
  }

  @Override
  public String toString() {
    return key;
  }
}
