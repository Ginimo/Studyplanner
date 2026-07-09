package ch.ost.mas_se.oop.studyplanner.ui;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.StudyModuleService;
import ch.ost.mas_se.oop.studyplanner.ui.forms.DegreeProgramForm;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleListView;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.format.annotation.DateTimeFormat;
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
  public String showDetails(
      @PathVariable final String degreeProgramId,
      @RequestParam(value = "nameFilter", required = false) final String nameFilter,
      @RequestParam(value = "offeredAt", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          final LocalDate offeredAt,
      @RequestParam(value = "sortBy", required = false) final String sortBy,
      @RequestParam(value = "sortDir", required = false) final String sortDir,
      final Model model) {

    DegreeProgram program = requireDegreeProgram(degreeProgramId);

    model.addAttribute("degreeProgram", DegreeProgramView.from(program));

    model.addAttribute(
        "studyModules",
        studyModuleService.findAllStudyModulesByDegreeProgramId(degreeProgramId).stream()
            .map(
                m ->
                    new StudyModuleListView(
                        m.getStudyModuleKey(),
                        m.getName(),
                        m.getEcts(),
                        m.getOfferedFrom(),
                        m.getOfferedTo(),
                        !m.getPrerequisiteKeys().isEmpty(),
                        m.getPrerequisiteKeys().size()))
            .collect(Collectors.toList()));

    model.addAttribute("nameFilter", nameFilter != null ? nameFilter : "");
    model.addAttribute("offeredAt", offeredAt != null ? offeredAt : LocalDate.now());
    model.addAttribute("sortBy", sortBy != null ? sortBy : "NAME");
    model.addAttribute("sortDir", sortDir != null ? sortDir : "ASC");

    model.addAttribute(
        "studyModules",
        MapFilterAndSort(
            studyModuleService.findAllStudyModulesByDegreeProgramId(degreeProgramId),
            nameFilter,
            offeredAt,
            sortBy,
            sortDir));
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
  @PostMapping("/{degreeProgramId}/delete")
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

  /**
   * Optionally filters the passed-in study modules by name and start date, sorts them according to
   * the specified sort parameters, and then converts them into StudyModuleListView objects for the
   * list view.
   */
  private List<StudyModuleListView> MapFilterAndSort(
      List<StudyModule> modules,
      String nameFilter,
      LocalDate offeredAt,
      String sortBy,
      String sortDir) {

    return modules.stream()
        .filter(
            m -> nameFilter == null || m.getName().toLowerCase().contains(nameFilter.toLowerCase()))
        .filter(
            m ->
                offeredAt == null
                    || ((m.getOfferedFrom() == null
                            || !m.getOfferedFrom()
                                .isAfter(offeredAt)) // OfferedFrom is before than FilterEntry
                        && (m.getOfferedTo() == null
                            || !m.getOfferedTo()
                                .isBefore(offeredAt)))) // OfferedTo is later than FilterEntry
        .sorted(comparatorSort(sortBy, sortDir))
        .map(
            m ->
                new StudyModuleListView(
                    m.getStudyModuleKey(),
                    m.getName(),
                    m.getEcts(),
                    m.getOfferedFrom(),
                    m.getOfferedTo(),
                    !m.getPrerequisiteKeys().isEmpty(),
                    m.getPrerequisiteKeys().size()))
        .collect(Collectors.toList());
  }

  /**
   * Creates a comparator based on the desired sort field and sort direction. The function supports
   * sorting by name, ECTS and course period.
   */
  private static Comparator<StudyModule> comparatorSort(
      final String sortBy, final String sortDirection) {
    Comparator<StudyModule> comparator;
    boolean desc = "DESC".equals(sortDirection);

    if (sortBy == null) {
      comparator = (a, b) -> a.getName().compareTo(b.getName());
    } else {
      comparator =
          switch (sortBy) {
            case "NAME" ->
                (a, b) ->
                    desc ? b.getName().compareTo(a.getName()) : a.getName().compareTo(b.getName());

            case "ECTS" ->
                (a, b) ->
                    desc
                        ? Integer.compare(b.getEcts(), a.getEcts())
                        : Integer.compare(a.getEcts(), b.getEcts());

            case "OFFERED_FROM" ->
                (a, b) -> compareNullable(a.getOfferedFrom(), b.getOfferedFrom(), desc, true);

            case "OFFERED_TO" ->
                (a, b) -> compareNullable(a.getOfferedTo(), b.getOfferedTo(), desc, false);

            default -> (a, b) -> a.getName().compareTo(b.getName());
          };
    }
    return comparator;
  }

  /** Compares nullable comparable values with configurable sort direction and null ordering. */
  private static <T extends Comparable<T>> int compareNullable(
      T valueA, T valueB, boolean desc, boolean nullsFirst) {
    int result;
    if (valueA == null && valueB == null) {
      result = 0;
    } else if (valueA == null) {
      result = nullsFirst ? -1 : 1;
    } else if (valueB == null) {
      result = nullsFirst ? 1 : -1;
    } else {
      result = valueA.compareTo(valueB);
    }
    return desc ? -result : result;
  }
}
