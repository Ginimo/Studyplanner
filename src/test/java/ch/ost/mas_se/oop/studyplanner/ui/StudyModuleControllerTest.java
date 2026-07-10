package ch.ost.mas_se.oop.studyplanner.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.ost.mas_se.oop.studyplanner.exception.DuplicateStudyModuleKeyException;
import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.service.DegreeProgramService;
import ch.ost.mas_se.oop.studyplanner.service.StudyModuleService;
import ch.ost.mas_se.oop.studyplanner.ui.forms.StudyModuleForm;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleView;
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
class StudyModuleControllerTest {

  private static final String PROGRAM_ID = "prog-1";
  private static final String STUDY_MODULE_KEY = "OOP1";

  @Mock private DegreeProgramService degreeProgramService;
  @Mock private StudyModuleService studyModuleService;
  @Mock private BindingResult bindingResult;

  @InjectMocks private StudyModuleController testee;

  private static DegreeProgram testProgram() {
    return new DegreeProgram(PROGRAM_ID, "Software Engineering", "", 6);
  }

  private static StudyModule testStudyModule() {
    return new StudyModule(STUDY_MODULE_KEY, PROGRAM_ID, "OOP 1", "", 4, List.of(), null, null);
  }

  @Test
  void showCreateForm_returnsFormViewWithEmptyForm() {
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    when(studyModuleService.findAllStudyModulesByDegreeProgramId(PROGRAM_ID)).thenReturn(List.of());
    var model = new ConcurrentModel();

    String view = testee.showCreateForm(PROGRAM_ID, model);

    assertThat(view).isEqualTo("studymodules/form");
    StudyModuleForm form = (StudyModuleForm) model.getAttribute("studyModule");
    assertThat(form).isNotNull();
    assertThat(form.isNewEntry()).isTrue();
  }

  @Test
  void createStudyModule_withValidForm_redirectsToProgram() {
    when(bindingResult.hasErrors()).thenReturn(false);
    StudyModuleForm form = new StudyModuleForm();
    form.setStudyModuleKey(STUDY_MODULE_KEY);
    form.setName("OOP 1");
    form.setEcts(3);
    var redirectAttributes = new RedirectAttributesModelMap();

    String view =
        testee.createStudyModule(
            PROGRAM_ID, form, bindingResult, new ConcurrentModel(), redirectAttributes);

    assertThat(view).isEqualTo("redirect:/degree-programs/" + PROGRAM_ID);
    assertThat(redirectAttributes.getFlashAttributes()).containsKey("successMessage");
  }

  @Test
  void createStudyModule_withDuplicateKey_returnsFormViewWithFieldError() {
    when(bindingResult.hasErrors()).thenReturn(false);
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    when(studyModuleService.findAllStudyModulesByDegreeProgramId(PROGRAM_ID)).thenReturn(List.of());
    doThrow(new DuplicateStudyModuleKeyException(STUDY_MODULE_KEY, PROGRAM_ID))
        .when(studyModuleService)
        .createStudyModule(
            eq(STUDY_MODULE_KEY), eq(PROGRAM_ID), any(), any(), anyInt(), any(), any(), any());
    StudyModuleForm form = new StudyModuleForm();
    form.setStudyModuleKey(STUDY_MODULE_KEY);
    form.setName("OOP 1");
    form.setEcts(3);

    String view =
        testee.createStudyModule(
            PROGRAM_ID,
            form,
            bindingResult,
            new ConcurrentModel(),
            new RedirectAttributesModelMap());

    assertThat(view).isEqualTo("studymodules/form");
    verify(bindingResult).rejectValue(eq("studyModuleKey"), any(), any());
  }

  @Test
  void createStudyModule_withInvalidForm_returnsFormView() {
    when(bindingResult.hasErrors()).thenReturn(true);
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    when(studyModuleService.findAllStudyModulesByDegreeProgramId(PROGRAM_ID)).thenReturn(List.of());

    String view =
        testee.createStudyModule(
            PROGRAM_ID,
            new StudyModuleForm(),
            bindingResult,
            new ConcurrentModel(),
            new RedirectAttributesModelMap());

    assertThat(view).isEqualTo("studymodules/form");
  }

  @Test
  void showDetails_returnsDetailsViewWithStudyModuleAndPrerequisites() {
    when(studyModuleService.findStudyModuleByKey(PROGRAM_ID, STUDY_MODULE_KEY))
        .thenReturn(Optional.of(testStudyModule()));
    when(studyModuleService.findStudyModulesByKeys(PROGRAM_ID, List.of())).thenReturn(List.of());
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    var model = new ConcurrentModel();

    String view = testee.showDetails(PROGRAM_ID, STUDY_MODULE_KEY, model);

    assertThat(view).isEqualTo("studymodules/details");
    assertThat(model.getAttribute("studyModule")).isInstanceOf(StudyModuleView.class);
    assertThat(model.getAttribute("prerequisites")).isNotNull();
  }

  @Test
  void showDetails_withUnknownStudyModuleKey_throwsNotFoundException() {
    when(studyModuleService.findStudyModuleByKey(any(), any())).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> testee.showDetails(PROGRAM_ID, STUDY_MODULE_KEY, new ConcurrentModel()))
        .isInstanceOf(ResponseStatusException.class);
  }

  @Test
  void showEditForm_returnsFormViewWithValuesFromModule() {
    when(studyModuleService.findStudyModuleByKey(PROGRAM_ID, STUDY_MODULE_KEY))
        .thenReturn(Optional.of(testStudyModule()));
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    when(studyModuleService.findAllStudyModulesByDegreeProgramId(PROGRAM_ID)).thenReturn(List.of());
    var model = new ConcurrentModel();

    String result = testee.showEditForm(PROGRAM_ID, STUDY_MODULE_KEY, model);

    assertThat(result).isEqualTo("studymodules/form");
    StudyModuleForm form = (StudyModuleForm) model.getAttribute("studyModule");
    assertThat(form).isNotNull();
    assertThat(form.isNewEntry()).isFalse();
    assertThat(form.getName()).isEqualTo("OOP 1");
  }

  @Test
  void updateStudyModule_withValidForm_redirectsToProgram() {
    when(studyModuleService.findStudyModuleByKey(PROGRAM_ID, STUDY_MODULE_KEY))
        .thenReturn(Optional.of(testStudyModule()));
    when(bindingResult.hasErrors()).thenReturn(false);
    StudyModuleForm form = new StudyModuleForm();
    form.setName("OOP 1");
    form.setEcts(3);
    var redirectAttributes = new RedirectAttributesModelMap();

    String view =
        testee.updateStudyModule(
            PROGRAM_ID,
            STUDY_MODULE_KEY,
            form,
            bindingResult,
            new ConcurrentModel(),
            redirectAttributes);

    assertThat(view).isEqualTo("redirect:/degree-programs/" + PROGRAM_ID);
    assertThat(redirectAttributes.getFlashAttributes()).containsKey("successMessage");
  }

  @Test
  void updateStudyModule_withInvalidForm_returnsFormView() {
    when(studyModuleService.findStudyModuleByKey(PROGRAM_ID, STUDY_MODULE_KEY))
        .thenReturn(Optional.of(testStudyModule()));
    when(bindingResult.hasErrors()).thenReturn(true);
    when(degreeProgramService.findDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgram()));
    when(studyModuleService.findAllStudyModulesByDegreeProgramId(PROGRAM_ID)).thenReturn(List.of());

    String view =
        testee.updateStudyModule(
            PROGRAM_ID,
            STUDY_MODULE_KEY,
            new StudyModuleForm(),
            bindingResult,
            new ConcurrentModel(),
            new RedirectAttributesModelMap());

    assertThat(view).isEqualTo("studymodules/form");
  }

  @Test
  void deleteStudyModule_redirectsToProgram() {
    when(studyModuleService.findStudyModuleByKey(PROGRAM_ID, STUDY_MODULE_KEY))
        .thenReturn(Optional.of(testStudyModule()));
    var redirectAttributes = new RedirectAttributesModelMap();

    String view = testee.deleteStudyModule(PROGRAM_ID, STUDY_MODULE_KEY, redirectAttributes);

    assertThat(view).isEqualTo("redirect:/degree-programs/" + PROGRAM_ID);
    assertThat(redirectAttributes.getFlashAttributes()).containsKey("successMessage");
  }
}
