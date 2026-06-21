package ch.ost.mas_se.oop.studyplanner.ui.views;

import java.time.LocalDate;

public class StudyModuleListView {

  private final StudyModuleKey studyModuleKey;
  private final String name;
  private final int ects;
  private final LocalDate offeredFrom;
  private final LocalDate offeredTo;
  private final boolean hasPrerequisites;
  private final int prerequisiteCount;

  public StudyModuleListView(
      final StudyModuleKey studyModuleKey,
      final String name,
      final int ects,
      final LocalDate offeredFrom,
      final LocalDate offeredTo,
      final boolean hasPrerequisites,
      final int prerequisiteCount) {
    this.studyModuleKey = studyModuleKey;
    this.name = name;
    this.ects = ects;
    this.offeredFrom = offeredFrom;
    this.offeredTo = offeredTo;
    this.hasPrerequisites = hasPrerequisites;
    this.prerequisiteCount = prerequisiteCount;
  }

  public StudyModuleKey getStudyModuleKey() {
    return studyModuleKey;
  }

  public String getName() {
    return name;
  }

  public int getEcts() {
    return ects;
  }

  public LocalDate getOfferedFrom() {
    return offeredFrom;
  }

  public LocalDate getOfferedTo() {
    return offeredTo;
  }

  public boolean isHasPrerequisites() {
    return hasPrerequisites;
  }

  public int getPrerequisiteCount() {
    return prerequisiteCount;
  }
}
