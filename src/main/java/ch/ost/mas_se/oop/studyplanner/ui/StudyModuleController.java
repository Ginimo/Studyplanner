package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.StudyModuleService;
import ch.ost.mas_se.oop.studyplanner.ui.forms.StudyModuleForm;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleKey;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/degree-programs/{degreeProgramId}/study-modules")
public class StudyModuleController {

    private final DegreeProgramService degreeProgramService;
    private final StudyModuleService studyModuleService;

    public StudyModuleController(final DegreeProgramService degreeProgramService, final StudyModuleService studyModuleService) {
        this.degreeProgramService = degreeProgramService;
        this.studyModuleService = studyModuleService;
    }

    /** Renders the blank creation form for a new study module. */
    @GetMapping("/new")
    public String showCreateForm(@PathVariable final String degreeProgramId, final Model model) {
        StudyModuleForm form = new StudyModuleForm();
        model.addAttribute("studyModule", form);
        return showCreateFormView(degreeProgramId, form, model);
    }

    /**
     * Processes the creation form. On validation errors the form is re-displayed;
     * on success the module is saved and the user is redirected to the degree program's detail view.
     */
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

        StudyModule module = toModule(form.getStudyModuleKey(), degreeProgramId, form);
        studyModuleService.create(module);

        redirectAttributes.addFlashAttribute("successMessage",
                "Study module \"" + form.getName() + "\" created.");
        return "redirect:/degree-programs/" + degreeProgramId;
    }

    /**
     * Renders the detail view for a single study module, including its resolved
     * prerequisite objects (dangling keys for deleted modules are silently skipped).
     */
    @GetMapping("/{studyModuleKey}")
    public String showDetails(@PathVariable final String degreeProgramId,
                              @PathVariable final String studyModuleKey,
                              final Model model) {
        StudyModule module = requireStudyModule(degreeProgramId, studyModuleKey);

        List<StudyModuleView> prerequisites = module.getPrerequisiteKeys().stream()
                .flatMap(key -> studyModuleService.findByKey(degreeProgramId, key).stream())
                .map(this::toView)
                .collect(Collectors.toList());

        model.addAttribute("degreeProgram", toView(requireProgram(degreeProgramId)));
        model.addAttribute("studyModule", toView(module));
        model.addAttribute("prerequisites", prerequisites);
        return "studymodules/details";
    }

    /** Loads the existing study module into the edit form. */
    @GetMapping("/{studyModuleKey}/edit")
    public String showEditForm(@PathVariable final String degreeProgramId,
                               @PathVariable final String studyModuleKey,
                               final Model model) {

        StudyModule module = requireStudyModule(degreeProgramId, studyModuleKey);

        StudyModuleForm form = new StudyModuleForm();
        form.setStudyModuleKey(module.getStudyModuleKey());
        form.setName(module.getName());
        form.setDescription(module.getDescription());
        form.setEcts(module.getEcts());
        form.setPrerequisiteKeys(module.getPrerequisiteKeys());
        form.setOfferedFrom(module.getOfferedFrom());
        form.setOfferedTo(module.getOfferedTo());

        model.addAttribute("studyModule", form);
        return showEditFormView(degreeProgramId, studyModuleKey, form, model);
    }

    /**
     * Processes the edit form. On validation errors the form is re-displayed;
     * on success the module is updated and the user is redirected to the degree program's detail view.
     */
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

        StudyModule module = toModule(studyModuleKey, degreeProgramId, form);
        studyModuleService.update(module);

        redirectAttributes.addFlashAttribute("successMessage",
                "Study module \"" + form.getName() + "\" updated.");
        return "redirect:/degree-programs/" + degreeProgramId;
    }

    /**
     * Deletes the study module, removing its key from the prerequisite lists of any
     * sibling modules, then redirects to the degree program's detail view.
     */
    @PostMapping("/{studyModuleKey}/delete")
    public String deleteStudyModule(@PathVariable final String degreeProgramId,
                                    @PathVariable final String studyModuleKey,
                                    final RedirectAttributes redirectAttributes) {
        StudyModule module = requireStudyModule(degreeProgramId, studyModuleKey);

        studyModuleService.delete(degreeProgramId, studyModuleKey);
        
        redirectAttributes.addFlashAttribute("successMessage",
                "Study module \"" + module.getName() + "\" deleted.");
        return "redirect:/degree-programs/" + degreeProgramId;
    }

    /** Populates the model with metadata for the create form and returns the form view name. */
    private String showCreateFormView(final String degreeProgramId, final StudyModuleForm form,
                                      final Model model) {
        form.setIsNew(true);
        model.addAttribute("degreeProgram", toView(requireProgram(degreeProgramId)));
        model.addAttribute("formAction", "/degree-programs/" + degreeProgramId + "/study-modules/new");
        model.addAttribute("pageTitle", "New Study Module");
        addAvailableStudyModulesForPrerequisiteSelection(degreeProgramId, null, model);
        return "studymodules/form";
    }

    /** Populates the model with metadata for the edit form and returns the form view name. */
    private String showEditFormView(final String degreeProgramId, final String studyModuleKey,
                                    final StudyModuleForm form, final Model model) {
        form.setIsNew(false);
        model.addAttribute("degreeProgram", toView(requireProgram(degreeProgramId)));
        model.addAttribute("formAction",
                "/degree-programs/" + degreeProgramId + "/study-modules/" + studyModuleKey + "/edit");
        model.addAttribute("pageTitle", "Edit Study Module");
        addAvailableStudyModulesForPrerequisiteSelection(degreeProgramId, studyModuleKey, model);
        return "studymodules/form";
    }

    /**
     * Populates the model with the study modules eligible for selection as prerequisites.
     * The module currently being edited ({@code excludeStudyModuleKey}) is excluded to prevent
     * a module from listing itself as its own prerequisite. Pass {@code null} when creating a
     * new module (nothing to exclude).
     */
    private void addAvailableStudyModulesForPrerequisiteSelection(final String degreeProgramId,
                                                                final String excludeStudyModuleKey,
                                                                final Model model) {
        List<StudyModuleView> available = studyModuleService
                .findAllByDegreeProgramId(degreeProgramId)
                .stream()
                .filter(sm -> !sm.getStudyModuleKey().equals(excludeStudyModuleKey))
                .map(this::toView)
                .collect(Collectors.toList());
        model.addAttribute("availableStudyModules", available);
    }

    /** Looks up a study module by key, throwing a 404 {@link ResponseStatusException} if it does not exist. */
    private StudyModule requireStudyModule(final String degreeProgramId, final String studyModuleKey) {
        return studyModuleService.findByKey(degreeProgramId, studyModuleKey)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study module not found: " + studyModuleKey));
    }

    /** Builds a {@link StudyModule} domain object from a form, using the given key (create vs. update supply different keys). */
    private StudyModule toModule(final String key, final String degreeProgramId, final StudyModuleForm form) {
        return new StudyModule(key, degreeProgramId, form.getName(), form.getDescription(),
                form.getEcts(), form.getPrerequisiteKeys(), form.getOfferedFrom(), form.getOfferedTo());
    }

    /** Looks up a degree program by ID, throwing a 404 {@link org.springframework.web.server.ResponseStatusException} if it does not exist. */
    private DegreeProgram requireProgram(final String id) {
        return degreeProgramService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Degree program not found: " + id));
    }

    /** Converts a {@link DegreeProgram} domain object to its view model. */
    private DegreeProgramView toView(DegreeProgram program) {
        return new DegreeProgramView(program.getId(), program.getName(), program.getDescription(), program.getMaxDurationInSemesters());
    }

    /** Converts a {@link StudyModule} domain object to its view model. */
    private StudyModuleView toView(StudyModule module) {
        return new StudyModuleView(new StudyModuleKey(module.getStudyModuleKey()), module.getName(), module.getDescription(), module.getEcts(), module.getPrerequisiteKeys());
    }
}
