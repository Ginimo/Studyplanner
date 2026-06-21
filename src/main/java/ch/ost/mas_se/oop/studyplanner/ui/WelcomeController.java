package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

  private final DegreeProgramService degreeProgramService;

  public WelcomeController(DegreeProgramService degreeProgramService) {
    this.degreeProgramService = degreeProgramService;
  }

  /** Renders the welcome page with the total number of registered degree programs. */
  @GetMapping("/")
  public String welcome(Model model) {
    model.addAttribute("programCount", degreeProgramService.findAll().size());
    return "welcome";
  }
}
