package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.StudyModuleService;
import ch.ost.mas_se.oop.studyplanner.service.StudyModuleServiceImpl;
import ch.ost.mas_se.oop.studyplanner.ui.forms.StudyModuleForm;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleView;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/degree-programs/{degreeProgramId}/study-modules")
public class StudyModuleController {

    private final DegreeProgramService degree_program_service;
    private final StudyModuleService module_service;

    public StudyModuleController(final DegreeProgramService degree_program_service,
                                 final StudyModuleService module_service) {
        this.degree_program_service = degree_program_service;
        this.module_service = module_service;
    }

    @GetMapping("/new")
    public String showCreateForm(@PathVariable final String degreeProgramId, final Model model) {
        StudyModuleForm form = new StudyModuleForm();
        model.addAttribute("studyModule", form);
        return showCreateFormView(degreeProgramId, form, model);
    }

    @PostMapping("/new")
    public String createStudyModule(
            @PathVariable final String degreeProgramId,
            @Valid @ModelAttribute("studyModule") final StudyModuleForm form,
            final BindingResult result,
            final Model model,
            final RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return showCreateFormView(degreeProgramId, form, model);
        }

        module_service.create(form,degreeProgramId);
        redirectAttributes.addFlashAttribute("successMessage",
                "Study module \"" + form.getName() + "\" created.");
        return "redirect:/degree-programs/" + degreeProgramId;
    }

    @GetMapping("/{studyModuleKey}")
    public String showDetails(@PathVariable final String degreeProgramId,
                              @PathVariable final String studyModuleKey,
                              final Model model) {
        StudyModuleView studyModule = requireStudyModule(studyModuleKey);

        List<StudyModuleView> prerequisites = studyModule.getPrerequisiteKeys().stream()
                .flatMap(key -> module_service.findViewByKey(key).stream())
                .toList();


        model.addAttribute("degreeProgram", requireProgram(degreeProgramId));
        model.addAttribute("studyModule", studyModule);
        model.addAttribute("prerequisites", prerequisites);
        return "studymodules/details";
    }

    @GetMapping("/{studyModuleKey}/edit")
    public String showEditForm(@PathVariable final String degreeProgramId,
                               @PathVariable final String studyModuleKey,
                               final Model model) {

        // TODO creation of view and form needs to be replaced (too much code here)
        StudyModuleView view = requireStudyModule(studyModuleKey);
        StudyModuleForm form = new StudyModuleForm();
        form.setStudyModuleKey(view.getStudyModuleKey().key());
        form.setName(view.getName());
        form.setDescription(view.getDescription());
        form.setEcts(view.getEcts());
        form.setPrerequisiteKeys(view.getPrerequisiteKeys());

        model.addAttribute("studyModule", form);
        return showEditFormView(degreeProgramId, studyModuleKey, form, model);
    }

    @PostMapping("/{studyModuleKey}/edit")
    public String updateStudyModule(
            @PathVariable final String degreeProgramId,
            @PathVariable final String studyModuleKey,
            @Valid @ModelAttribute("studyModule") final StudyModuleForm form,
            final BindingResult result,
            final Model model,
            final RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return showEditFormView(degreeProgramId, studyModuleKey, form, model);
        }

        module_service.update(form, studyModuleKey);
        redirectAttributes.addFlashAttribute("successMessage",
                "Study module \"" + form.getName() + "\" updated.");
        return "redirect:/degree-programs/" + degreeProgramId;
    }

    @PostMapping("/{studyModuleKey}/delete")
    public String deleteStudyModule(@PathVariable final String degreeProgramId,
                                    @PathVariable final String studyModuleKey,
                                    final RedirectAttributes redirectAttributes) {
        StudyModuleView studyModule = requireStudyModule(studyModuleKey);
        module_service.delete(studyModuleKey);
        redirectAttributes.addFlashAttribute("successMessage",
                "Study module \"" + studyModule.getName() + "\" deleted.");
        return "redirect:/degree-programs/" + degreeProgramId;
    }

    private String showCreateFormView(final String degreeProgramId, final StudyModuleForm form,
                                      final Model model) {
        form.setIsNew(true);
        model.addAttribute("degreeProgram", requireProgram(degreeProgramId));
        model.addAttribute("formAction", "/degree-programs/" + degreeProgramId + "/study-modules/new");
        model.addAttribute("pageTitle", "New Study Module");
        addAvailableStudyModules4PrerequisiteSelection(degreeProgramId, null, model);
        return "studymodules/form";
    }

    private String showEditFormView(final String degreeProgramId, final String studyModuleKey,
                                    final StudyModuleForm form, final Model model) {
        form.setIsNew(false);
        model.addAttribute("degreeProgram", requireProgram(degreeProgramId));
        model.addAttribute("formAction",
                "/degree-programs/" + degreeProgramId + "/study-modules/" + studyModuleKey + "/edit");
        model.addAttribute("pageTitle", "Edit Study Module");
        addAvailableStudyModules4PrerequisiteSelection(degreeProgramId, studyModuleKey, model);
        return "studymodules/form";
    }

    private void addAvailableStudyModules4PrerequisiteSelection(final String degreeProgramId,
                                                                final String excludeStudyModuleKey,
                                                                final Model model) {
        List<StudyModuleView> available = module_service
                .findViewAll(degreeProgramId)
                .stream()
                .filter(sm -> !sm.getStudyModuleKey().key().equals(excludeStudyModuleKey))
                .toList();
        model.addAttribute("availableStudyModules", available);
    }

    private DegreeProgramView requireProgram(final String id) {
        return degree_program_service.findViewById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Degree program not found: " + id));

    }

    private StudyModuleView requireStudyModule(final String studyModuleKey) {
        return module_service.findViewByKey(studyModuleKey)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Study module not found: " + studyModuleKey));
    }
}
