package ch.ost.mas_se.oop.studyplanner.ui.views;

import java.util.Objects;

public record StudyModuleKey(String value) {

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    StudyModuleKey that = (StudyModuleKey) other;
    return Objects.equals(value, that.value);
  }

  @Override
  public String toString() {
    return value;
  }
}
