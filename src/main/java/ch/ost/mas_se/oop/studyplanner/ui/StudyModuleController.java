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

        StudyModule module = new StudyModule(
                form.getStudyModuleKey(),
                degreeProgramId,
                form.getName(),
                form.getDescription(),
                form.getEcts(),
                form.getPrerequisiteKeys(),
                form.getOfferedFrom(),
                form.getOfferedTo()
        );
        studyModuleService.create(module);

        redirectAttributes.addFlashAttribute("successMessage",
                "Study module \"" + form.getName() + "\" created.");
        return "redirect:/degree-programs/" + degreeProgramId;
    }

    @GetMapping("/{studyModuleKey}")
    public String showDetails(@PathVariable final String degreeProgramId,
                              @PathVariable final String studyModuleKey,
                              final Model model) {
        StudyModule module = studyModuleService.findByKey(degreeProgramId, studyModuleKey)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study module not found: " + studyModuleKey));

        List<StudyModuleView> prerequisites = module.getPrerequisiteKeys().stream()
                .flatMap(key -> studyModuleService.findByKey(degreeProgramId, key).stream())
                .map(this::toView)
                .collect(Collectors.toList());

        model.addAttribute("degreeProgram", toView(requireProgram(degreeProgramId)));
        model.addAttribute("studyModule", toView(module));
        model.addAttribute("prerequisites", prerequisites);
        return "studymodules/details";
    }

    @GetMapping("/{studyModuleKey}/edit")
    public String showEditForm(@PathVariable final String degreeProgramId,
                               @PathVariable final String studyModuleKey,
                               final Model model) {

        StudyModule module = studyModuleService.findByKey(degreeProgramId, studyModuleKey)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study module not found: " + studyModuleKey));

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

        StudyModule module = new StudyModule(
                studyModuleKey,
                degreeProgramId,
                form.getName(),
                form.getDescription(),
                form.getEcts(),
                form.getPrerequisiteKeys(),
                form.getOfferedFrom(),
                form.getOfferedTo()
        );
        studyModuleService.update(module);

        redirectAttributes.addFlashAttribute("successMessage",
                "Study module \"" + form.getName() + "\" updated.");
        return "redirect:/degree-programs/" + degreeProgramId;
    }

    @PostMapping("/{studyModuleKey}/delete")
    public String deleteStudyModule(@PathVariable final String degreeProgramId,
                                    @PathVariable final String studyModuleKey,
                                    final RedirectAttributes redirectAttributes) {
        StudyModule module = studyModuleService.findByKey(degreeProgramId, studyModuleKey)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study module not found: " + studyModuleKey));

        studyModuleService.delete(degreeProgramId, studyModuleKey);
        
        redirectAttributes.addFlashAttribute("successMessage",
                "Study module \"" + module.getName() + "\" deleted.");
        return "redirect:/degree-programs/" + degreeProgramId;
    }

    private String showCreateFormView(final String degreeProgramId, final StudyModuleForm form,
                                      final Model model) {
        form.setIsNew(true);
        model.addAttribute("degreeProgram", toView(requireProgram(degreeProgramId)));
        model.addAttribute("formAction", "/degree-programs/" + degreeProgramId + "/study-modules/new");
        model.addAttribute("pageTitle", "New Study Module");
        addAvailableStudyModulesForPrerequisiteSelection(degreeProgramId, null, model);
        return "studymodules/form";
    }

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

    private DegreeProgram requireProgram(final String id) {
        return degreeProgramService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Degree program not found: " + id));
    }

    private DegreeProgramView toView(DegreeProgram program) {
        return new DegreeProgramView(program.getId(), program.getName(), program.getDescription(), program.getMaxDurationInSemesters());
    }

    private StudyModuleView toView(StudyModule module) {
        return new StudyModuleView(new StudyModuleKey(module.getStudyModuleKey()), module.getName(), module.getDescription(), module.getEcts(), module.getPrerequisiteKeys());
    }
}
