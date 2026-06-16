package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.exception.CyclicDependencyException;
import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.model.Semester;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.ScheduleService;
import ch.ost.mas_se.oop.studyplanner.ui.views.ScheduleView;
import ch.ost.mas_se.oop.studyplanner.ui.views.SemesterView;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleKey;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleView;
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
    model.addAttribute("degreeProgram", toView(program));

    scheduleService
        .findByDegreeProgramId(degreeProgramId)
        .ifPresent(schedule -> addScheduleToModel(schedule, model));

    return "schedules/view";
  }

  /** Adds the {@link ScheduleView} and the list of {@link SemesterView}s to the model. */
  private void addScheduleToModel(final Schedule schedule, final Model model) {
    model.addAttribute(
        "schedule", new ScheduleView(schedule.getCalculatedAt(), schedule.getTotalSemesters()));
    model.addAttribute(
        "semesterViews",
        schedule.getSemesters().stream().map(this::toView).collect(Collectors.toList()));
  }

  /** Converts a {@link Semester} domain object to its view model. */
  private SemesterView toView(final Semester semester) {
    return new SemesterView(
        semester.getSemesterNumber(),
        semester.getStudyModules().stream()
            .map(
                m ->
                    new StudyModuleView(
                        new StudyModuleKey(m.getStudyModuleKey()),
                        m.getName(),
                        m.getDescription(),
                        m.getEcts(),
                        m.getPrerequisiteKeys()))
            .collect(Collectors.toList()),
        semester.getTotalEcts(),
        semester.getStudyModules().stream()
            .map(StudyModule::getStudyModuleKey)
            .collect(Collectors.joining(", ")));
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
