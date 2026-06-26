package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.StudyModuleService;
import ch.ost.mas_se.oop.studyplanner.ui.forms.DegreeProgramForm;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleListView;
import jakarta.validation.Valid;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/degree-programs")
public class DegreeProgramController extends AbstractController {

  private final StudyModuleService studyModuleService;

  public DegreeProgramController(
      final DegreeProgramService degreeProgramService,
      final StudyModuleService studyModuleService) {
    super(degreeProgramService);
    this.studyModuleService = studyModuleService;
  }

  /** Renders the list of all degree programs. */
  @GetMapping
  public String listDegreePrograms(final Model model) {
    model.addAttribute(
        "degreePrograms",
        degreeProgramService.findAllDegreePrograms().stream()
            .map(DegreeProgramView::from)
            .collect(Collectors.toList()));
    return "degreeprograms/list";
  }

  /** Renders the blank creation form for a new degree program. */
  @GetMapping("/new")
  public String showCreateForm(final Model model) {
    model.addAttribute("degreeProgram", new DegreeProgramForm());
    return showCreateFormView(model);
  }

  /**
   * Processes the creation form. On validation errors the form is re-displayed; on success the
   * program is saved and the user is redirected to the program list.
   */
  @PostMapping("/new")
  public String createDegreeProgram(
      @Valid @ModelAttribute("degreeProgram") final DegreeProgramForm form,
      final BindingResult result,
      final Model model,
      final RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
      return showCreateFormView(model);
    }

    degreeProgramService.createDegreeProgram(
        form.getName(), form.getDescription(), form.getMaxDurationInSemesters());

    redirectAttributes.addFlashAttribute(
        "successMessage", "Degree program \"" + form.getName() + "\" created.");
    return "redirect:/degree-programs";
  }

  /**
   * Renders the detail view for a degree program, including its study modules filtered and sorted
   * according to the query parameters.
   */
  @GetMapping("/{degreeProgramId}")
  public String showDetails(@PathVariable final String degreeProgramId, final Model model) {
    DegreeProgram program = requireDegreeProgram(degreeProgramId);

    model.addAttribute("degreeProgram", DegreeProgramView.from(program));

    model.addAttribute(
        "studyModules",
        studyModuleService.findAllStudyModulesByDegreeProgramId(degreeProgramId).stream()
            .map(StudyModuleListView::from)
            .collect(Collectors.toList()));

    return "degreeprograms/details";
  }

  /** Loads the existing degree program into the edit form. */
  @GetMapping("/{degreeProgramId}/edit")
  public String showEditForm(@PathVariable final String degreeProgramId, final Model model) {
    DegreeProgram program = requireDegreeProgram(degreeProgramId);

    DegreeProgramForm form = new DegreeProgramForm();
    form.setName(program.getName());
    form.setDescription(program.getDescription());
    form.setMaxDurationInSemesters(program.getMaxDurationInSemesters());

    model.addAttribute("degreeProgram", form);
    return showEditFormView(degreeProgramId, model);
  }

  /**
   * Processes the edit form. On validation errors the form is re-displayed; on success the program
   * is updated and the user is redirected to its detail view.
   */
  @PostMapping("/{degreeProgramId}/edit")
  public String updateDegreeProgram(
      @PathVariable final String degreeProgramId,
      @Valid @ModelAttribute("degreeProgram") final DegreeProgramForm form,
      final BindingResult result,
      final Model model,
      final RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
      return showEditFormView(degreeProgramId, model);
    }

    degreeProgramService.updateDegreeProgram(
        degreeProgramId, form.getName(), form.getDescription(), form.getMaxDurationInSemesters());

    redirectAttributes.addFlashAttribute("successMessage", "Degree program updated.");
    return "redirect:/degree-programs/" + degreeProgramId;
  }

  /** Deletes the degree program together with all of its study modules and schedule. */
  @PostMapping("/{degreeProgramId}/deleteDegreeProgram")
  public String deleteDegreeProgram(
      @PathVariable final String degreeProgramId, final RedirectAttributes redirectAttributes) {
    DegreeProgram program = requireDegreeProgram(degreeProgramId);
    degreeProgramService.deleteDegreeProgram(degreeProgramId);
    redirectAttributes.addFlashAttribute(
        "successMessage", "Degree program \"" + program.getName() + "\" deleted.");
    return "redirect:/degree-programs";
  }

  /**
   * Populates the model with metadata for the createDegreeProgram form and returns the form view
   * name.
   */
  private String showCreateFormView(final Model model) {
    model.addAttribute("formAction", "/degree-programs/new");
    model.addAttribute("pageTitle", "New Degree Program");
    return "degreeprograms/form";
  }

  /** Populates the model with metadata for the edit form and returns the form view name. */
  private String showEditFormView(final String degreeProgramId, final Model model) {
    model.addAttribute("formAction", "/degree-programs/" + degreeProgramId + "/edit");
    model.addAttribute("pageTitle", "Edit Degree Program");
    return "degreeprograms/form";
  }
}
