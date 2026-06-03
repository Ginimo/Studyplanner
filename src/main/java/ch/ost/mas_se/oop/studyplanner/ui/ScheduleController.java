package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.exception.CyclicDependencyException;
import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.ScheduleService;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import ch.ost.mas_se.oop.studyplanner.ui.views.ScheduleView;
import ch.ost.mas_se.oop.studyplanner.ui.views.SemesterView;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleKey;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleView;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/degree-programs/{degreeProgramId}/schedule")
public class ScheduleController {

    private final DegreeProgramService degreeProgramService;
    private final ScheduleService scheduleService;

    public ScheduleController(final DegreeProgramService degreeProgramService, final ScheduleService scheduleService) {
        this.degreeProgramService = degreeProgramService;
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public String showSchedule(@PathVariable final String degreeProgramId, final Model model) {
        DegreeProgram program = requireProgram(degreeProgramId);
        model.addAttribute("degreeProgram", toView(program));

        Schedule schedule = (Schedule) model.getAttribute("flashSchedule");
        if (schedule == null && !model.containsAttribute("errorMessage")) {
            try {
                schedule = scheduleService.calculate(program);
            } catch (CyclicDependencyException e) {
                model.addAttribute("errorMessage", e.getMessage());
            }
        }

        if (schedule != null) {
            model.addAttribute("schedule", new ScheduleView(schedule.getCalculatedAt(), schedule.getTotalSemesters()));
            model.addAttribute("semesterViews", schedule.getSemesters().stream()
                    .map(s -> new SemesterView(
                            s.getSemesterNumber(),
                            s.getStudyModules().stream()
                                    .map(m -> new StudyModuleView(
                                            new StudyModuleKey(m.getStudyModuleKey()),
                                            m.getName(),
                                            m.getDescription(),
                                            m.getEcts(),
                                            m.getPrerequisiteKeys()
                                    ))
                                    .collect(Collectors.toList()),
                            s.getTotalEcts(),
                            s.getStudyModules().stream()
                                    .map(StudyModule::getStudyModuleKey)
                                    .collect(Collectors.joining(", "))
                    ))
                    .collect(Collectors.toList()));
        }

        return "schedules/view";
    }

    @PostMapping("/calculate")
    public String calculateSchedule(@PathVariable final String degreeProgramId,
                                    final RedirectAttributes redirectAttributes) {
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

    private DegreeProgram requireProgram(final String degreeProgramId) {
        return degreeProgramService.findById(degreeProgramId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Degree program not found for id: " + degreeProgramId));
    }

    private DegreeProgramView toView(DegreeProgram program) {
        return new DegreeProgramView(program.getId(), program.getName(), program.getDescription(), program.getMaxDurationInSemesters());
    }
}
