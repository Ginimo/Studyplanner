package ch.ost.mas_se.oop.studyplanner.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    private final DummyDataGenerator dummyDataGenerator;

    public WelcomeController(DummyDataGenerator dummyDataGenerator) {
        this.dummyDataGenerator = dummyDataGenerator;
    }

    @GetMapping("/")
    public String welcome(Model model) {
        // TODO replace by real lookup
        model.addAttribute("programCount", dummyDataGenerator.getListOfDegreePrograms().size());
        return "welcome";
    }
}
