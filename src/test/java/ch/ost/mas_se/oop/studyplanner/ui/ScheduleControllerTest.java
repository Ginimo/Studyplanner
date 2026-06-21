package ch.ost.mas_se.oop.studyplanner.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import ch.ost.mas_se.oop.studyplanner.ui.views.ScheduleView;
import ch.ost.mas_se.oop.studyplanner.ui.views.SemesterView;
import java.time.LocalDateTime;
import java.util.List;
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

  @Mock private DummyDataGenerator dummyDataGenerator;

  @InjectMocks private ScheduleController testee;

  private static DegreeProgramView testProgramView() {
    return new DegreeProgramView(PROGRAM_ID, "Software Engineering", "", 6);
  }

  @Test
  void showSchedule_withoutExistingSchedule_returnsViewWithProgramOnly() {
    DegreeProgramView programView = testProgramView();
    when(dummyDataGenerator.getDegreeProgramById(PROGRAM_ID)).thenReturn(Optional.of(programView));
    when(dummyDataGenerator.getScheduleViewForProgram(PROGRAM_ID)).thenReturn(Optional.empty());
    var model = new ConcurrentModel();

    String view = testee.showSchedule(PROGRAM_ID, model);

    assertThat(view).isEqualTo("schedules/view");
    assertThat(model.getAttribute("degreeProgram")).isSameAs(programView);
    assertThat(model.getAttribute("schedule")).isNull();
    assertThat(model.getAttribute("semesterViews")).isNull();
  }

  @Test
  void showSchedule_withExistingSchedule_addsScheduleAndSemesterViewsToModel() {
    DegreeProgramView programView = testProgramView();
    ScheduleView scheduleView = new ScheduleView(LocalDateTime.now(), 3);
    List<SemesterView> semesterViews = List.of();
    when(dummyDataGenerator.getDegreeProgramById(PROGRAM_ID)).thenReturn(Optional.of(programView));
    when(dummyDataGenerator.getScheduleViewForProgram(PROGRAM_ID))
        .thenReturn(Optional.of(scheduleView));
    when(dummyDataGenerator.getSemesterViewForProgram(PROGRAM_ID)).thenReturn(semesterViews);
    var model = new ConcurrentModel();

    String view = testee.showSchedule(PROGRAM_ID, model);

    assertThat(view).isEqualTo("schedules/view");
    assertThat(model.getAttribute("schedule")).isSameAs(scheduleView);
    assertThat(model.getAttribute("semesterViews")).isSameAs(semesterViews);
  }

  @Test
  void calculateSchedule_redirectsWithSuccessMessage() {
    when(dummyDataGenerator.getDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgramView()));
    var redirectAttributes = new RedirectAttributesModelMap();

    String view = testee.calculateSchedule(PROGRAM_ID, redirectAttributes);

    assertThat(view).isEqualTo("redirect:/degree-programs/" + PROGRAM_ID + "/schedule");
    assertThat(redirectAttributes.getFlashAttributes()).containsKey("successMessage");
  }
}
