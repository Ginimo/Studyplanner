package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.StudyModuleService;
import ch.ost.mas_se.oop.studyplanner.ui.forms.DegreeProgramForm;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleKey;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleListView;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/degree-programs")
public class DegreeProgramController {

    private final DegreeProgramService degreeProgramService;
    private final StudyModuleService studyModuleService;

    public DegreeProgramController(final DegreeProgramService degreeProgramService, final StudyModuleService studyModuleService) {
        this.degreeProgramService = degreeProgramService;
        this.studyModuleService = studyModuleService;
    }

    @GetMapping
    public String listDegreePrograms(final Model model) {
        model.addAttribute("degreePrograms", degreeProgramService.findAll().stream()
                .map(this::toView)
                .collect(Collectors.toList()));
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

        DegreeProgram program = new DegreeProgram(
                UUID.randomUUID().toString(),
                form.getName(),
                form.getDescription(),
                form.getMaxDurationInSemesters()
        );
        degreeProgramService.create(program);

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

        DegreeProgram program = degreeProgramService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Degree program not found: " + id));

        model.addAttribute("degreeProgram", toView(program));

        model.addAttribute("studyModules", studyModuleService.findAllByDegreeProgramId(id).stream()
                .map(m -> new StudyModuleListView(
                        new StudyModuleKey(m.getStudyModuleKey()),
                        m.getName(),
                        m.getEcts(),
                        m.getOfferedFrom(),
                        m.getOfferedTo(),
                        !m.getPrerequisiteKeys().isEmpty(),
                        m.getPrerequisiteKeys().size()
                ))
                .collect(Collectors.toList()));

        model.addAttribute("nameFilter", nameFilter != null ? nameFilter : "");
        model.addAttribute("offeredAt", offeredAt != null ? offeredAt : LocalDate.now());
        model.addAttribute("sortBy", sortBy != null ? sortBy : "NAME");
        model.addAttribute("sortDir", sortDir != null ? sortDir : "ASC");
        return "degreeprograms/details";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable final String id, final Model model) {
        DegreeProgram program = degreeProgramService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Degree program not found: " + id));

        DegreeProgramForm form = new DegreeProgramForm();
        form.setName(program.getName());
        form.setDescription(program.getDescription());
        form.setMaxDurationInSemesters(program.getMaxDurationInSemesters());

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

        DegreeProgram program = new DegreeProgram(
                id,
                form.getName(),
                form.getDescription(),
                form.getMaxDurationInSemesters()
        );
        degreeProgramService.update(program);

        redirectAttributes.addFlashAttribute("successMessage", "Degree program updated.");
        return "redirect:/degree-programs/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteDegreeProgram(@PathVariable final String id,
                                      final RedirectAttributes redirectAttributes) {
        DegreeProgram program = degreeProgramService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Degree program not found: " + id));
        
        studyModuleService.findAllByDegreeProgramId(id)
                .forEach(m -> studyModuleService.delete(id, m.getStudyModuleKey()));
        degreeProgramService.delete(id);

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

    private DegreeProgramView toView(DegreeProgram program) {
        return new DegreeProgramView(program.getId(), program.getName(), program.getDescription(), program.getMaxDurationInSemesters());
    }
}
