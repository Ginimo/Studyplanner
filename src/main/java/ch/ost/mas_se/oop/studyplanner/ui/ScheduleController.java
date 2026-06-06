package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.SemesterService;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ch.ost.mas_se.oop.studyplanner.service.ScheduleService;


@Controller
@RequestMapping("/degree-programs/{degreeProgramId}/schedule")
public class ScheduleController {

    private final ScheduleService schedule_service;
    private final SemesterService semester_service;
    private DegreeProgramService degree_program_service;


    public ScheduleController(DegreeProgramService degree_program_service,
                              ScheduleService scheduleService,
                              SemesterService semester_service) {
        this.degree_program_service = degree_program_service;
        schedule_service = scheduleService;
        this.semester_service = semester_service;
    }

    @GetMapping
    public String showSchedule(@PathVariable final String degreeProgramId, final Model model) {
        DegreeProgramView degreeProgram = requireProgram(degreeProgramId);
        model.addAttribute("degreeProgram", degreeProgram);

        schedule_service.findById(degreeProgramId).ifPresent(scheduleView -> {
            model.addAttribute("schedule", scheduleView);
            model.addAttribute("semesterViews",
                    semester_service.findViewAll(degreeProgramId));
        });

        return "schedules/view";
    }

    @PostMapping("/calculate")
    public String calculateSchedule(@PathVariable final String degreeProgramId,
                                    final RedirectAttributes redirectAttributes) {
        requireProgram(degreeProgramId);
        Schedule schedule = this.schedule_service.calculate(degreeProgramId);

        // TODO for Kursblock 6: catch exception that is thrown when cyclic study module dependencies are detected
        // You can print errors on the UI as follows: redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        // (e is a reference to your exception)

        redirectAttributes.addFlashAttribute("successMessage",
                "Study plan calculated successfully.");
        return "redirect:/degree-programs/" + degreeProgramId + "/schedule";
    }


    private DegreeProgramView requireProgram(final String id) {
        return degree_program_service.findViewById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Degree program not found: " + id));

    }
}
