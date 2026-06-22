package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.exception.CyclicDependencyException;
import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.model.Semester;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.ScheduleService;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import ch.ost.mas_se.oop.studyplanner.ui.views.ScheduleView;
import ch.ost.mas_se.oop.studyplanner.ui.views.SemesterView;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleKey;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleView;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/degree-programs/{degreeProgramId}/schedule")
public class ScheduleController {

  private final DegreeProgramService degreeProgramService;
  private final ScheduleService scheduleService;

  public ScheduleController(
      final DegreeProgramService degreeProgramService, final ScheduleService scheduleService) {
    this.degreeProgramService = degreeProgramService;
    this.scheduleService = scheduleService;
  }

  /**
   * Renders the schedule view. If a pre-calculated {@link Schedule} was passed as a flash attribute
   * by {@link #calculateSchedule} (via POST/Redirect/GET), it is used directly; otherwise the
   * schedule is (re-)calculated on the fly so the page is always populated on a plain GET.
   */
  @GetMapping
  public String showSchedule(@PathVariable final String degreeProgramId, final Model model) {
    DegreeProgram program = requireProgram(degreeProgramId);
    model.addAttribute("degreeProgram", toView(program));

    Schedule schedule = resolveSchedule(program, model);
    if (schedule != null) {
       addScheduleToModel(schedule, model);
    }

    return "schedules/view";
  }

  /**
   * Returns the schedule to display: the flash-attribute schedule from a preceding POST if one was
   * passed, a freshly calculated schedule otherwise. Returns {@code null} and adds {@code
   * errorMessage} to the model when a cycle is detected.
   */
  private Schedule resolveSchedule(final DegreeProgram program, final Model model) {
    Schedule schedule = (Schedule) model.getAttribute("flashSchedule");
    if (schedule == null && !model.containsAttribute("errorMessage")) {
      try {
        schedule = scheduleService.calculate(program);
      } catch (CyclicDependencyException e) {
        model.addAttribute("errorMessage", e.getMessage());
      }
    }
    return schedule;
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
   * Calculates the schedule and stores the result as a flash attribute before redirecting to {@link
   * #showSchedule}, so the computed schedule survives the redirect without being recalculated a
   * second time.
   */
  @PostMapping("/calculate")
  public String calculateSchedule(
      @PathVariable final String degreeProgramId, final RedirectAttributes redirectAttributes) {
    DegreeProgram program = requireProgram(degreeProgramId);
    try {
      Schedule schedule = scheduleService.calculate(program);
      redirectAttributes.addFlashAttribute("flashSchedule", schedule);
      redirectAttributes.addFlashAttribute("successMessage", "Study plan calculated successfully.");
    } catch (CyclicDependencyException e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    }
    return "redirect:/degree-programs/" + degreeProgramId + "/schedule";
  }

  /**
   * Looks up a degree program by ID, throwing a 404 {@link ResponseStatusException} if it does not
   * exist.
   */
  private DegreeProgram requireProgram(final String degreeProgramId) {
    return degreeProgramService
        .findById(degreeProgramId)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Degree program not found for id: " + degreeProgramId));
  }

  /** Converts a {@link DegreeProgram} domain object to its view model. */
  private DegreeProgramView toView(DegreeProgram program) {
    return new DegreeProgramView(
        program.getId(),
        program.getName(),
        program.getDescription(),
        program.getMaxDurationInSemesters());
  }
}
