package ch.ost.mas_se.oop.studyplanner.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.ost.mas_se.oop.studyplanner.exception.DuplicateDegreeProgramNameException;
import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.StudyModuleService;
import ch.ost.mas_se.oop.studyplanner.ui.forms.DegreeProgramForm;
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
    form.setMaxDurationInSemesters(6);
    var redirectAttributes = new RedirectAttributesModelMap();

    String view =
        testee.createDegreeProgram(form, bindingResult, new ConcurrentModel(), redirectAttributes);

    assertThat(view).isEqualTo("redirect:/degree-programs");
    assertThat(redirectAttributes.getFlashAttributes()).containsKey("successMessage");
  }

  @Test
  void createDegreeProgram_withDuplicateName_returnsFormViewWithFieldError() {
    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(new DuplicateDegreeProgramNameException("Software Engineering"))
        .when(degreeProgramService)
        .createDegreeProgram(eq("Software Engineering"), any(), anyInt());
    DegreeProgramForm form = new DegreeProgramForm();
    form.setName("Software Engineering");
    form.setMaxDurationInSemesters(6);

    String view =
        testee.createDegreeProgram(
            form, bindingResult, new ConcurrentModel(), new RedirectAttributesModelMap());

    assertThat(view).isEqualTo("degreeprograms/form");
    verify(bindingResult).rejectValue(eq("name"), any(), any());
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
    when(studyModuleService.findFilteredStudyModules(any(), any(), any(), any(), any()))
        .thenReturn(List.of());
    var model = new ConcurrentModel();

    String view = testee.showDetails(PROGRAM_ID, model, "", null, "NAME", "ASC");

    assertThat(view).isEqualTo("degreeprograms/details");
    assertThat(model.getAttribute("degreeProgram")).isNotNull();
    assertThat(model.getAttribute("studyModules")).isNotNull();
  }

  @Test
  void showDetails_withUnknownId_throwsNotFoundException() {
    when(degreeProgramService.findDegreeProgramById(any())).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> testee.showDetails("unknown-id", new ConcurrentModel(), "", null, "NAME", "ASC"))
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
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    when(bindingResult.hasErrors()).thenReturn(false);
    var redirectAttributes = new RedirectAttributesModelMap();

    DegreeProgramForm form = new DegreeProgramForm();
    form.setName("Software Engineering");
    form.setMaxDurationInSemesters(6);

    String view =
        testee.updateDegreeProgram(
            PROGRAM_ID, form, bindingResult, new ConcurrentModel(), redirectAttributes);

    assertThat(view).isEqualTo("redirect:/degree-programs/" + PROGRAM_ID);
    assertThat(redirectAttributes.getFlashAttributes()).containsKey("successMessage");
  }

  @Test
  void updateDegreeProgram_withDuplicateName_returnsFormViewWithFieldError() {
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(new DuplicateDegreeProgramNameException("Software Engineering"))
        .when(degreeProgramService)
        .updateDegreeProgram(eq(PROGRAM_ID), eq("Software Engineering"), any(), anyInt());
    DegreeProgramForm form = new DegreeProgramForm();
    form.setName("Software Engineering");
    form.setMaxDurationInSemesters(6);

    String view =
        testee.updateDegreeProgram(
            PROGRAM_ID,
            form,
            bindingResult,
            new ConcurrentModel(),
            new RedirectAttributesModelMap());

    assertThat(view).isEqualTo("degreeprograms/form");
    verify(bindingResult).rejectValue(eq("name"), any(), any());
  }

  @Test
  void updateDegreeProgram_withInvalidForm_returnsFormView() {
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
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
