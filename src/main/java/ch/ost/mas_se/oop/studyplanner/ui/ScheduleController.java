package ch.ost.mas_se.oop.studyplanner.ui;

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

@Controller
@RequestMapping("/degree-programs/{degreeProgramId}/schedule")
public class ScheduleController {

    private final DummyDataGenerator dummyDataGenerator;

    public ScheduleController(final DummyDataGenerator dummyDataGenerator) {
        this.dummyDataGenerator = dummyDataGenerator;
    }

    @GetMapping
    public String showSchedule(@PathVariable final String degreeProgramId, final Model model) {
        DegreeProgramView degreeProgram = requireProgram(degreeProgramId);
        model.addAttribute("degreeProgram", degreeProgram);

        // TODO to be replaced
        dummyDataGenerator.getScheduleViewForProgram(degreeProgramId).ifPresent(scheduleView -> {
            model.addAttribute("schedule", scheduleView);
            model.addAttribute("semesterViews",
                    dummyDataGenerator.getSemesterViewForProgram(degreeProgramId));
        });

        return "schedules/view";
    }

    @PostMapping("/calculate")
    public String calculateSchedule(@PathVariable final String degreeProgramId,
                                    final RedirectAttributes redirectAttributes) {
        requireProgram(degreeProgramId);
        // TODO: call component that calculates the schedule

        // TODO for Kursblock 6: catch exception that is thrown when cyclic study module dependencies are detected
        // You can print errors on the UI as follows: redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        // (e is a reference to your exception)

        redirectAttributes.addFlashAttribute("successMessage",
                "Study plan calculated successfully.");
        return "redirect:/degree-programs/" + degreeProgramId + "/schedule";
    }

    private DegreeProgramView requireProgram(final String degreeProgramId) {
        return dummyDataGenerator.getDegreeProgramById(degreeProgramId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Degree program not found for id: " + degreeProgramId));
    }
}
