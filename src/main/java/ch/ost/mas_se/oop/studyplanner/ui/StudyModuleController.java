package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.StudyModuleService;
import ch.ost.mas_se.oop.studyplanner.ui.forms.StudyModuleForm;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleView;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/degree-programs/{degreeProgramId}/study-modules")
public class StudyModuleController extends AbstractController {

  private final StudyModuleService studyModuleService;

  public StudyModuleController(
      final DegreeProgramService degreeProgramService,
      final StudyModuleService studyModuleService) {
    super(degreeProgramService);
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
   * Processes the creation form. On validation errors the form is re-displayed; on success the
   * module is saved and the user is redirected to the degree program's detail view.
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

    studyModuleService.createStudyModule(
        form.getStudyModuleKey(),
        degreeProgramId,
        form.getName(),
        form.getDescription(),
        form.getEcts(),
        form.getPrerequisiteKeys(),
        form.getOfferedFrom(),
        form.getOfferedTo());

    redirectAttributes.addFlashAttribute(
        "successMessage", "Study module \"" + form.getName() + "\" created.");
    return "redirect:/degree-programs/" + degreeProgramId;
  }

  /**
   * Renders the detail view for a single study module, including its resolved prerequisite objects
   * (dangling keys for deleted modules are silently skipped).
   */
  @GetMapping("/{studyModuleKey}")
  public String showDetails(
      @PathVariable final String degreeProgramId,
      @PathVariable final String studyModuleKey,
      final Model model) {
    StudyModule module = requireStudyModule(degreeProgramId, studyModuleKey);

    List<StudyModuleView> prerequisites =
        studyModuleService
            .findStudyModulesByKeys(degreeProgramId, module.getPrerequisiteKeys())
            .stream()
            .map(StudyModuleView::from)
            .collect(Collectors.toList());

    model.addAttribute("degreeProgram", DegreeProgramView.from(requireProgram(degreeProgramId)));
    model.addAttribute("studyModule", StudyModuleView.from(module));
    model.addAttribute("prerequisites", prerequisites);
    return "studymodules/details";
  }

  /** Loads the existing study module into the edit form. */
  @GetMapping("/{studyModuleKey}/edit")
  public String showEditForm(
      @PathVariable final String degreeProgramId,
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
   * Processes the edit form. On validation errors the form is re-displayed; on success the module
   * is updated and the user is redirected to the degree program's detail view.
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

    studyModuleService.updateStudyModule(
        studyModuleKey,
        degreeProgramId,
        form.getName(),
        form.getDescription(),
        form.getEcts(),
        form.getPrerequisiteKeys(),
        form.getOfferedFrom(),
        form.getOfferedTo());

    redirectAttributes.addFlashAttribute(
        "successMessage", "Study module \"" + form.getName() + "\" updated.");
    return "redirect:/degree-programs/" + degreeProgramId;
  }

  /**
   * Deletes the study module, removing its key from the prerequisite lists of any sibling modules,
   * then redirects to the degree program's detail view.
   */
  @PostMapping("/{studyModuleKey}/deleteStudyModule")
  public String deleteStudyModule(
      @PathVariable final String degreeProgramId,
      @PathVariable final String studyModuleKey,
      final RedirectAttributes redirectAttributes) {
    StudyModule module = requireStudyModule(degreeProgramId, studyModuleKey);

    studyModuleService.deleteStudyModule(degreeProgramId, studyModuleKey);

    redirectAttributes.addFlashAttribute(
        "successMessage", "Study module \"" + module.getName() + "\" deleted.");
    return "redirect:/degree-programs/" + degreeProgramId;
  }

  /**
   * Populates the model with metadata for the createDegreeProgram form and returns the form view
   * name.
   */
  private String showCreateFormView(
      final String degreeProgramId, final StudyModuleForm form, final Model model) {
    form.setNewEntry(true);
    model.addAttribute("degreeProgram", DegreeProgramView.from(requireProgram(degreeProgramId)));
    model.addAttribute("formAction", "/degree-programs/" + degreeProgramId + "/study-modules/new");
    model.addAttribute("pageTitle", "New Study Module");
    addAvailableStudyModulesForPrerequisiteSelection(degreeProgramId, null, model);
    return "studymodules/form";
  }

  /** Populates the model with metadata for the edit form and returns the form view name. */
  private String showEditFormView(
      final String degreeProgramId,
      final String studyModuleKey,
      final StudyModuleForm form,
      final Model model) {
    form.setNewEntry(false);
    model.addAttribute("degreeProgram", DegreeProgramView.from(requireProgram(degreeProgramId)));
    model.addAttribute(
        "formAction",
        "/degree-programs/" + degreeProgramId + "/study-modules/" + studyModuleKey + "/edit");
    model.addAttribute("pageTitle", "Edit Study Module");
    addAvailableStudyModulesForPrerequisiteSelection(degreeProgramId, studyModuleKey, model);
    return "studymodules/form";
  }

  /**
   * Populates the model with the study modules eligible for selection as prerequisites. The module
   * currently being edited ({@code excludeStudyModuleKey}) is excluded to prevent a module from
   * listing itself as its own prerequisite. Pass {@code null} when creating a new module (nothing
   * to exclude).
   */
  private void addAvailableStudyModulesForPrerequisiteSelection(
      final String degreeProgramId, final String excludeStudyModuleKey, final Model model) {
    List<StudyModuleView> available =
        studyModuleService.findAllStudyModulesByDegreeProgramId(degreeProgramId).stream()
            .filter(sm -> !sm.getStudyModuleKey().equals(excludeStudyModuleKey))
            .map(StudyModuleView::from)
            .collect(Collectors.toList());
    model.addAttribute("availableStudyModules", available);
  }

  /**
   * Looks up a study module by key, throwing a 404 {@link ResponseStatusException} if it does not
   * exist.
   */
  private StudyModule requireStudyModule(
      final String degreeProgramId, final String studyModuleKey) {
    return studyModuleService
        .findStudyModuleByKey(degreeProgramId, studyModuleKey)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Study module not found: " + studyModuleKey));
  }
}
