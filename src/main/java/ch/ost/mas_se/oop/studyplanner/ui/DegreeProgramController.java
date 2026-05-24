package ch.ost.mas_se.oop.studyplanner.ui;

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

@Controller
@RequestMapping("/degree-programs") // part of the url
public class DegreeProgramController {

    private final DummyDataGenerator dummyDataGenerator;

    public DegreeProgramController(final DummyDataGenerator dummyDataGenerator) {
        this.dummyDataGenerator = dummyDataGenerator; // Objekt
    }

    @GetMapping
    public String listDegreePrograms(final Model model) {
        // TODO to be replaced
        model.addAttribute("degreePrograms", dummyDataGenerator.getListOfDegreePrograms()); // Array Liste
        return "degreeprograms/list";
    }

    @GetMapping("/new")
    public String showCreateForm(final Model model) {
        model.addAttribute("degreeProgram", new DegreeProgramForm()); // Geht auf Form
        return showCreateFormView(model);
    }

    @PostMapping("/new")
    public String createDegreeProgram(
            @Valid @ModelAttribute("degreeProgram") final DegreeProgramForm form, // @Valid löst die Validierung aus (@NotBlank, @Min) und @ModelAttribute befüllt das form-Objekt mit den eingegebenen Werten
            final BindingResult result, // Validierung und Exception Catching (vom Spring MVC Framework)
            final Model model, // Model= Speicher für ein Request. Sobald Template gerendet ist, ist das Model weg (vom Spring MVC Framework)
            final RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return showCreateFormView(model);
        }

        // TODO: build a DegreeProgram from the form and save it
        redirectAttributes.addFlashAttribute("successMessage", // addFlashAttribute speichert Daten kurz zwischen zwei Requests:
                "Degree program \"" + form.getName() + "\" created.");
        return "redirect:/degree-programs";
    }

    @GetMapping("/{id}")
    public String showDetails(
            // kommen von der URL: z.B. http://localhost:8080/degree-programs/sample-id-1234?sortBy=NAME&sortDir=ASC&nameFilter=&offeredAt=2026-05-24
            @PathVariable final String id,
            @RequestParam(value = "nameFilter", required = false) final String nameFilter,
            @RequestParam(value = "offeredAt", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate offeredAt,
            @RequestParam(value = "sortBy", required = false) final String sortBy,
            @RequestParam(value = "sortDir", required = false) final String sortDir,
            final Model model) {

        DegreeProgramView program = requireProgram(id); // Objekt von View (Über ID) holen

        model.addAttribute("degreeProgram", program);
        // TODO to be replaced
        model.addAttribute("studyModules",
                dummyDataGenerator.getStudyModuleListViewEntriesByProgramId(id)); // alle Module dieses Programms ins Model
        // Die Filter/Sort-Werte zurück ins Model damit das Suchfeld mit dem eingegebenen Wert befüllt & den gewählten Sort beibehält
        //Sonst würde nach dem Filtern das Suchfeld leer sein.:
        model.addAttribute("nameFilter", nameFilter != null ? nameFilter : "");
        model.addAttribute("offeredAt", offeredAt != null ? offeredAt : LocalDate.now());
        model.addAttribute("sortBy", sortBy != null ? sortBy : "NAME");
        model.addAttribute("sortDir", sortDir != null ? sortDir : "ASC");
        return "degreeprograms/details";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable final String id, final Model model) {
        // TODO creation of view and form needs to be replaced
        DegreeProgramView view = requireProgram(id); // Holt sich das Objekt in View das zu dieser ID gehört
        DegreeProgramForm form = new DegreeProgramForm(); // macht ein neues Objekt in Form
        // Bestehende Werte von View ins Form kopieren:
        form.setName(view.getName());
        form.setDescription(view.getDescription());
        form.setMaxDurationInSemesters(view.getMaxDurationInSemesters());

        model.addAttribute("degreeProgram", form); // Legt das ausgefüllte Form ins Modell, weil der User anpassen soll
        return showEditFormView(id, model);
    }

    @PostMapping("/{id}/edit")
    public String updateDegreeProgram(
            @PathVariable final String id, // id kommt aus URL: http://localhost:8080/degree-programs/sample-id-1234/edit
            @Valid @ModelAttribute("degreeProgram") final DegreeProgramForm form,   // Spring befüllt form mit den eingegebenen Werte // @Valid löst Validierung aus
            final BindingResult result,
            final Model model,
            final RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return showEditFormView(id, model); // zeigt es an, sodass nochmal eine Eingabe erfolgt
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
        return dummyDataGenerator.getDegreeProgramById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Degree program not found: " + id));
    }
}
