package ch.ost.mas_se.oop.studyplanner.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.ost.mas_se.oop.studyplanner.ui.forms.StudyModuleForm;
import ch.ost.mas_se.oop.studyplanner.ui.views.DegreeProgramView;
import ch.ost.mas_se.oop.studyplanner.ui.views.StudyModuleKey;
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

  @Mock private DummyDataGenerator dummyDataGenerator;
  @Mock private BindingResult bindingResult;

  @InjectMocks private StudyModuleController testee;

  private static DegreeProgramView testProgramView() {
    return new DegreeProgramView(PROGRAM_ID, "Software Engineering", "", 6);
  }

  private static StudyModuleView testStudyModuleView() {
    return new StudyModuleView(new StudyModuleKey(STUDY_MODULE_KEY), "OOP 1", "", 4, List.of());
  }

  @Test
  void showCreateForm_returnsFormViewWithEmptyForm() {
    when(dummyDataGenerator.getDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgramView()));
    when(dummyDataGenerator.getStudyModuleViewByProgramId(PROGRAM_ID)).thenReturn(List.of());
    var model = new ConcurrentModel();

    String view = testee.showCreateForm(PROGRAM_ID, model);

    assertThat(view).isEqualTo("studymodules/form");
    StudyModuleForm form = (StudyModuleForm) model.getAttribute("studyModule");
    assertThat(form).isNotNull();
    assertThat(form.getIsNew()).isTrue();
  }

  @Test
  void createStudyModule_withValidForm_redirectsToProgram() {
    when(bindingResult.hasErrors()).thenReturn(false);
    StudyModuleForm form = new StudyModuleForm();
    form.setName("OOP 1");
    var redirectAttributes = new RedirectAttributesModelMap();

    String view =
        testee.createStudyModule(
            PROGRAM_ID, form, bindingResult, new ConcurrentModel(), redirectAttributes);

    assertThat(view).isEqualTo("redirect:/degree-programs/" + PROGRAM_ID);
    assertThat(redirectAttributes.getFlashAttributes()).containsKey("successMessage");
  }

  @Test
  void createStudyModule_withInvalidForm_returnsFormView() {
    when(bindingResult.hasErrors()).thenReturn(true);
    when(dummyDataGenerator.getDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgramView()));
    when(dummyDataGenerator.getStudyModuleViewByProgramId(PROGRAM_ID)).thenReturn(List.of());

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
    StudyModuleView studyModuleView = testStudyModuleView();
    when(dummyDataGenerator.getStudyModuleViewByKey(STUDY_MODULE_KEY))
        .thenReturn(Optional.of(studyModuleView));
    when(dummyDataGenerator.getDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgramView()));
    var model = new ConcurrentModel();

    String view = testee.showDetails(PROGRAM_ID, STUDY_MODULE_KEY, model);

    assertThat(view).isEqualTo("studymodules/details");
    assertThat(model.getAttribute("studyModule")).isSameAs(studyModuleView);
    assertThat(model.getAttribute("prerequisites")).isNotNull();
  }

  @Test
  void showDetails_withUnknownStudyModuleKey_throwsNotFoundException() {
    when(dummyDataGenerator.getStudyModuleViewByKey(any())).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> testee.showDetails(PROGRAM_ID, STUDY_MODULE_KEY, new ConcurrentModel()))
        .isInstanceOf(ResponseStatusException.class);
  }

  @Test
  void showEditForm_returnsFormViewWithValuesFromView() {
    StudyModuleView view = testStudyModuleView();
    when(dummyDataGenerator.getStudyModuleViewByKey(STUDY_MODULE_KEY))
        .thenReturn(Optional.of(view));
    when(dummyDataGenerator.getDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgramView()));
    when(dummyDataGenerator.getStudyModuleViewByProgramId(PROGRAM_ID)).thenReturn(List.of());
    var model = new ConcurrentModel();

    String result = testee.showEditForm(PROGRAM_ID, STUDY_MODULE_KEY, model);

    assertThat(result).isEqualTo("studymodules/form");
    StudyModuleForm form = (StudyModuleForm) model.getAttribute("studyModule");
    assertThat(form).isNotNull();
    assertThat(form.getIsNew()).isFalse();
    assertThat(form.getName()).isEqualTo("OOP 1");
  }

  @Test
  void updateStudyModule_withValidForm_redirectsToProgram() {
    when(bindingResult.hasErrors()).thenReturn(false);
    StudyModuleForm form = new StudyModuleForm();
    form.setName("OOP 1");
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
    when(bindingResult.hasErrors()).thenReturn(true);
    when(dummyDataGenerator.getDegreeProgramById(PROGRAM_ID))
        .thenReturn(Optional.of(testProgramView()));
    when(dummyDataGenerator.getStudyModuleViewByProgramId(PROGRAM_ID)).thenReturn(List.of());

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
    when(dummyDataGenerator.getStudyModuleViewByKey(STUDY_MODULE_KEY))
        .thenReturn(Optional.of(testStudyModuleView()));
    var redirectAttributes = new RedirectAttributesModelMap();

    String view = testee.deleteStudyModule(PROGRAM_ID, STUDY_MODULE_KEY, redirectAttributes);

    assertThat(view).isEqualTo("redirect:/degree-programs/" + PROGRAM_ID);
    assertThat(redirectAttributes.getFlashAttributes()).containsKey("successMessage");
  }
}
