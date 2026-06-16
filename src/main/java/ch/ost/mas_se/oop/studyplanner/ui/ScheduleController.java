package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.exception.CyclicDependencyException;
import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.ScheduleService;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import ch.ost.mas_se.oop.studyplanner.ui.views.ScheduleView;
import ch.ost.mas_se.oop.studyplanner.ui.views.SemesterView;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/degree-programs/{degreeProgramId}/schedule")
public class ScheduleController extends AbstractController {

  private final ScheduleService scheduleService;

  public ScheduleController(
      final DegreeProgramService degreeProgramService, final ScheduleService scheduleService) {
    super(degreeProgramService);
    this.scheduleService = scheduleService;
  }

  /**
   * Renders the schedule view. Loads the last persisted schedule from the repository. If no
   * schedule has been calculated yet, the view is rendered without one.
   */
  @GetMapping
  public String showSchedule(@PathVariable final String degreeProgramId, final Model model) {
    DegreeProgram program = requireProgram(degreeProgramId);
    model.addAttribute("degreeProgram", DegreeProgramView.toView(program));

    scheduleService
        .findByDegreeProgramId(degreeProgramId)
        .ifPresent(schedule -> addScheduleToModel(schedule, model));

    return "schedules/view";
  }

  private void addScheduleToModel(final Schedule schedule, final Model model) {
    model.addAttribute("schedule", ScheduleView.toView(schedule));
    model.addAttribute(
        "semesterViews",
        schedule.getSemesters().stream().map(SemesterView::toView).collect(Collectors.toList()));
  }

  /**
   * Calculates the schedule, persists it, then redirects to {@link #showSchedule} which loads the
   * result from the repository.
   */
  @PostMapping("/calculate")
  public String calculateSchedule(
      @PathVariable final String degreeProgramId, final RedirectAttributes redirectAttributes) {
    DegreeProgram program = requireProgram(degreeProgramId);
    try {
      scheduleService.calculateAndSave(program);
      redirectAttributes.addFlashAttribute("successMessage", "Study plan calculated successfully.");
    } catch (CyclicDependencyException e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    }
    return "redirect:/degree-programs/" + degreeProgramId + "/schedule";
  }
}
