package ch.ost.mas_se.oop.studyplanner.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import ch.ost.mas_se.oop.studyplanner.exception.CyclicDependencyException;
import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.model.Semester;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.ScheduleService;
import ch.ost.mas_se.oop.studyplanner.service.StudyModuleService;
import ch.ost.mas_se.oop.studyplanner.ui.views.ScheduleView;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@ExtendWith(MockitoExtension.class)
class ScheduleControllerTest {

  private static final String PROGRAM_ID = "prog-1";

  @Mock private DegreeProgramService degreeProgramService;
  @Mock private ScheduleService scheduleService;
  @Mock private StudyModuleService studyModuleService;

  @InjectMocks private ScheduleController testee;

  private static DegreeProgram testProgram() {
    return new DegreeProgram(PROGRAM_ID, "Software Engineering", "", 6);
  }

  @Test
  void showSchedule_withoutExistingSchedule_returnsViewWithProgramOnly() {
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    when(scheduleService.findScheduleByDegreeProgramId(PROGRAM_ID)).thenReturn(Optional.empty());
    var model = new ConcurrentModel();

    String view = testee.showSchedule(PROGRAM_ID, model);

    assertThat(view).isEqualTo("schedules/view");
    assertThat(model.getAttribute("degreeProgram")).isNotNull();
    assertThat(model.getAttribute("schedule")).isNull();
    assertThat(model.getAttribute("semesterViews")).isNull();
  }

  @Test
  void showSchedule_withExistingSchedule_addsScheduleAndSemesterViewsToModel() {
    Schedule schedule = new Schedule(LocalDateTime.now(), List.of(new Semester(1, List.of())));
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    when(scheduleService.findScheduleByDegreeProgramId(PROGRAM_ID))
        .thenReturn(Optional.of(schedule));
    when(studyModuleService.findModuleMapByDegreeProgramId(PROGRAM_ID)).thenReturn(Map.of());
    var model = new ConcurrentModel();

    String view = testee.showSchedule(PROGRAM_ID, model);

    assertThat(view).isEqualTo("schedules/view");
    assertThat(model.getAttribute("schedule")).isInstanceOf(ScheduleView.class);
    assertThat(model.getAttribute("semesterViews")).isNotNull();
  }

  @Test
  void calculateSchedule_redirectsWithSuccessMessage() {
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    var redirectAttributes = new RedirectAttributesModelMap();

    String view = testee.calculateSchedule(PROGRAM_ID, redirectAttributes);

    assertThat(view).isEqualTo("redirect:/degree-programs/" + PROGRAM_ID + "/schedule");
    assertThat(redirectAttributes.getFlashAttributes()).containsKey("successMessage");
  }

  @Test
  void calculateSchedule_onCyclicDependencyException_redirectsWithErrorMessage() {
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    doThrow(new CyclicDependencyException(List.of("A", "B")))
        .when(scheduleService)
        .calculateAndSaveSchedule(any());
    var redirectAttributes = new RedirectAttributesModelMap();

    String view = testee.calculateSchedule(PROGRAM_ID, redirectAttributes);

    assertThat(view).isEqualTo("redirect:/degree-programs/" + PROGRAM_ID + "/schedule");
    assertThat(redirectAttributes.getFlashAttributes()).containsKey("errorMessage");
    assertThat(redirectAttributes.getFlashAttributes()).doesNotContainKey("successMessage");
  }
}
