package ch.ost.mas_se.oop.studyplanner.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.StudyModuleService;
import ch.ost.mas_se.oop.studyplanner.ui.forms.DegreeProgramForm;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleListView;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@ExtendWith(MockitoExtension.class)
class DegreeProgramControllerTest {

  private static final String PROGRAM_ID = "prog-1";

  @Mock private DegreeProgramService degreeProgramService;
  @Mock private StudyModuleService studyModuleService;
  @Mock private BindingResult bindingResult;

  @InjectMocks private DegreeProgramController testee;

  private static DegreeProgram testProgram() {
    return new DegreeProgram(PROGRAM_ID, "Software Engineering", "A great program.", 6);
  }

  @Test
  void listDegreePrograms_returnsListViewWithPrograms() {
    when(degreeProgramService.findAllDegreePrograms()).thenReturn(List.of(testProgram()));
    var model = new ConcurrentModel();

    String view = testee.listDegreePrograms(model);

    assertThat(view).isEqualTo("degreeprograms/list");
    assertThat(model.getAttribute("degreePrograms")).isNotNull();
  }

  @Test
  void showCreateForm_returnsFormViewWithEmptyForm() {
    var model = new ConcurrentModel();

    String view = testee.showCreateForm(model);

    assertThat(view).isEqualTo("degreeprograms/form");
    assertThat(model.getAttribute("degreeProgram")).isInstanceOf(DegreeProgramForm.class);
    assertThat(model.getAttribute("formAction")).isEqualTo("/degree-programs/new");
  }

  @Test
  void createDegreeProgram_withValidForm_redirectsToDegreePrograms() {
    when(bindingResult.hasErrors()).thenReturn(false);
    DegreeProgramForm form = new DegreeProgramForm();
    form.setName("Software Engineering");
    var redirectAttributes = new RedirectAttributesModelMap();

    String view =
        testee.createDegreeProgram(form, bindingResult, new ConcurrentModel(), redirectAttributes);

    assertThat(view).isEqualTo("redirect:/degree-programs");
    assertThat(redirectAttributes.getFlashAttributes()).containsKey("successMessage");
  }

  @Test
  void createDegreeProgram_withInvalidForm_returnsFormView() {
    when(bindingResult.hasErrors()).thenReturn(true);
    var model = new ConcurrentModel();

    String view =
        testee.createDegreeProgram(
            new DegreeProgramForm(), bindingResult, model, new RedirectAttributesModelMap());

    assertThat(view).isEqualTo("degreeprograms/form");
    assertThat(model.getAttribute("formAction")).isEqualTo("/degree-programs/new");
  }

  @Test
  void showDetails_returnsDetailsViewWithProgramAndStudyModules() {
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    when(studyModuleService.findAllStudyModulesByDegreeProgramId(PROGRAM_ID)).thenReturn(List.of());
    var model = new ConcurrentModel();

    String view =
        testee.showDetails(
            PROGRAM_ID,
            null, // nameFilter
            null, // offeredAt
            null, // sortBy
            null, // sortDir
            model);

    assertThat(view).isEqualTo("degreeprograms/details");
    assertThat(model.getAttribute("degreeProgram")).isNotNull();
    assertThat(model.getAttribute("studyModules")).isNotNull();
  }

  @Test
  void showDetails_addsFilterAndSortParametersToModel() {
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    when(studyModuleService.findAllStudyModulesByDegreeProgramId(PROGRAM_ID)).thenReturn(List.of());
    var model = new ConcurrentModel();

    String view =
        testee.showDetails(
            PROGRAM_ID,
            "Data", // nameFilter
            LocalDate.of(2020, 1, 8), // offeredAt
            "ECTS", // sortBy
            "DESC", // sortDir
            model);

    assertAll(
        () -> assertThat(view).isEqualTo("degreeprograms/details"),
        () -> assertThat(model.getAttribute("nameFilter")).isEqualTo("Data"),
        () -> assertThat(model.getAttribute("offeredAt")).isEqualTo(LocalDate.of(2020, 1, 8)),
        () -> assertThat(model.getAttribute("sortBy")).isEqualTo("ECTS"),
        () -> assertThat(model.getAttribute("sortDir")).isEqualTo("DESC"));
  }

  @Test
  void showDetails_sortsStudyModulesByEctsDescending() {
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));

    StudyModule moduleWith5Ects =
        new StudyModule("OOP1", PROGRAM_ID, "OOP 1", "", 5, List.of(), null, null);

    StudyModule moduleWith10Ects =
        new StudyModule("OOP2", PROGRAM_ID, "OOP 2", "", 10, List.of(), null, null);

    StudyModule moduleWith3Ects =
        new StudyModule("OOP3", PROGRAM_ID, "OOP 3", "", 3, List.of(), null, null);

    when(studyModuleService.findAllStudyModulesByDegreeProgramId(PROGRAM_ID))
        .thenReturn(List.of(moduleWith5Ects, moduleWith10Ects, moduleWith3Ects));

    var model = new ConcurrentModel();

    String view = testee.showDetails(PROGRAM_ID, null, null, "ECTS", "DESC", model);

    assertThat(view).isEqualTo("degreeprograms/details");

    @SuppressWarnings("unchecked")
    List<StudyModuleListView> studyModules =
        (List<StudyModuleListView>) model.getAttribute("studyModules");

    assertThat(studyModules)
        .extracting(StudyModuleListView::studyModuleKey)
        .containsExactly("OOP2", "OOP1", "OOP3");
  }

  @Test
  void showDetails_withUnknownId_throwsNotFoundException() {
    when(degreeProgramService.findDegreeProgramById(any())).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                testee.showDetails(
                    "unknown-id",
                    null, // nameFilter
                    null, // offeredAt
                    null, // sortBy
                    null, // sortDir
                    new ConcurrentModel()))
        .isInstanceOf(ResponseStatusException.class);
  }

  @Test
  void showEditForm_returnsFormViewWithValuesFromProgram() {
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    var model = new ConcurrentModel();

    String view = testee.showEditForm(PROGRAM_ID, model);

    assertThat(view).isEqualTo("degreeprograms/form");
    DegreeProgramForm form = (DegreeProgramForm) model.getAttribute("degreeProgram");
    assertThat(form).isNotNull();
    assertThat(form.getName()).isEqualTo("Software Engineering");
  }

  @Test
  void updateDegreeProgram_withValidForm_redirectsToDetails() {
    when(bindingResult.hasErrors()).thenReturn(false);
    var redirectAttributes = new RedirectAttributesModelMap();

    String view =
        testee.updateDegreeProgram(
            PROGRAM_ID,
            new DegreeProgramForm(),
            bindingResult,
            new ConcurrentModel(),
            redirectAttributes);

    assertThat(view).isEqualTo("redirect:/degree-programs/" + PROGRAM_ID);
    assertThat(redirectAttributes.getFlashAttributes()).containsKey("successMessage");
  }

  @Test
  void updateDegreeProgram_withInvalidForm_returnsFormView() {
    when(bindingResult.hasErrors()).thenReturn(true);
    var model = new ConcurrentModel();

    String view =
        testee.updateDegreeProgram(
            PROGRAM_ID,
            new DegreeProgramForm(),
            bindingResult,
            model,
            new RedirectAttributesModelMap());

    assertThat(view).isEqualTo("degreeprograms/form");
    assertThat(model.getAttribute("formAction"))
        .isEqualTo("/degree-programs/" + PROGRAM_ID + "/edit");
  }

  @Test
  void deleteDegreeProgram_redirectsToDegreePrograms() {
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    var redirectAttributes = new RedirectAttributesModelMap();

    String view = testee.deleteDegreeProgram(PROGRAM_ID, redirectAttributes);

    assertThat(view).isEqualTo("redirect:/degree-programs");
    assertThat(redirectAttributes.getFlashAttributes()).containsKey("successMessage");
  }
}
