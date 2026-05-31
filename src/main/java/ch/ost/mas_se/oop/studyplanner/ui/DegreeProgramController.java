package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramServiceImpl;
import ch.ost.mas_se.oop.studyplanner.service.StudyModuleService;
import ch.ost.mas_se.oop.studyplanner.ui.forms.DegreeProgramForm;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/degree-programs")
public class DegreeProgramController {

    private DegreeProgramService degree_program_service;
    private StudyModuleService module_service;


    public DegreeProgramController(final DegreeProgramService degree_program_service,
                                   final StudyModuleService module_service) {
        this.degree_program_service = degree_program_service;
        this.module_service = module_service;
    }


    @GetMapping
    public String listDegreePrograms(final Model model) {
        model.addAttribute("degreePrograms",degree_program_service.findAll());
        return "degreeprograms/list";
    }

    @GetMapping("/new")
    public String showCreateForm(final Model model) {
        model.addAttribute("degreeProgram", new DegreeProgramForm());
        return showCreateFormView(model);
    }

    @PostMapping("/new")
    public String createDegreeProgram(
            @Valid @ModelAttribute("degreeProgram") final DegreeProgramForm form,
            final BindingResult result,
            final Model model,
            final RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return showCreateFormView(model);
        }

        degree_program_service.create(form);
        redirectAttributes.addFlashAttribute("successMessage",
                "Degree program \"" + form.getName() + "\" created.");
        return "redirect:/degree-programs";
    }

    @GetMapping("/{id}")
    public String showDetails(
            @PathVariable final String id,
            @RequestParam(value = "nameFilter", required = false) final String nameFilter,
            @RequestParam(value = "offeredAt", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate offeredAt,
            @RequestParam(value = "sortBy", required = false) final String sortBy,
            @RequestParam(value = "sortDir", required = false) final String sortDir,
            final Model model) {

        DegreeProgramView program = requireProgram(id);
        IO.println("Programm ID: " + id);

        List<StudyModule> modules = module_service.findAll(id);
        IO.println("Modules: " +  modules);

        model.addAttribute("degreeProgram", program);
        model.addAttribute("studyModules",modules);
        model.addAttribute("nameFilter", nameFilter != null ? nameFilter : "");
        model.addAttribute("offeredAt", offeredAt != null ? offeredAt : LocalDate.now());
        model.addAttribute("sortBy", sortBy != null ? sortBy : "NAME");
        model.addAttribute("sortDir", sortDir != null ? sortDir : "ASC");
        return "degreeprograms/details";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable final String id, final Model model) {
        // TODO creation of view and form needs to be replaced
        DegreeProgramView view = requireProgram(id);
        DegreeProgramForm form = new DegreeProgramForm();
        form.setName(view.getName());
        form.setDescription(view.getDescription());
        form.setMaxDurationInSemesters(view.getMaxDurationInSemesters());

        model.addAttribute("degreeProgram", form);
        return showEditFormView(id, model);
    }

    @PostMapping("/{id}/edit")
    public String updateDegreeProgram(
            @PathVariable final String id,
            @Valid @ModelAttribute("degreeProgram") final DegreeProgramForm form,
            final BindingResult result,
            final Model model,
            final RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return showEditFormView(id, model);
        }

        // TODO: build a DegreeProgram from the form and save it
        redirectAttributes.addFlashAttribute("successMessage", "Degree program updated.");
        return "redirect:/degree-programs/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteDegreeProgram(@PathVariable final String id,
                                      final RedirectAttributes redirectAttributes) {
        DegreeProgramView program = requireProgram(id);
        // TODO: actually delete the degree program here
        redirectAttributes.addFlashAttribute("successMessage",
                "Degree program \"" + program.getName() + "\" deleted.");
        return "redirect:/degree-programs";
    }

    private String showCreateFormView(final Model model) {
        model.addAttribute("formAction", "/degree-programs/new");
        model.addAttribute("pageTitle", "New Degree Program");
        return "degreeprograms/form";
    }

    private String showEditFormView(final String id, final Model model) {
        model.addAttribute("formAction", "/degree-programs/" + id + "/edit");
        model.addAttribute("pageTitle", "Edit Degree Program");
        return "degreeprograms/form";
    }

    private DegreeProgramView requireProgram(final String id) {
        return degree_program_service.findViewById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Degree program not found: " + id));

    }

}
