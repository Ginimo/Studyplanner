package ch.ost.mas_se.oop.studyplanner;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.CacheMode;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@AnalyzeClasses(packagesOf = StudyplannerApplication.class, cacheMode = CacheMode.PER_CLASS)
class ArchitectureTest {

  private static final String BASE = "ch.ost.mas_se.oop.studyplanner";

  private static final DescribedPredicate<JavaClass> NOT_ABSTRACT =
      DescribedPredicate.describe(
          "not abstract", javaClass -> !javaClass.getModifiers().contains(JavaModifier.ABSTRACT));

  // ── Layer access rules ────────────────────────────────────────────────────

  @ArchTest
  static final ArchRule modelLayer_hasNoDependenciesOnOtherProjectLayers =
      noClasses()
          .that()
          .resideInAPackage(BASE + ".model..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(BASE + ".repository..", BASE + ".service..", BASE + ".ui..");

  @ArchTest
  static final ArchRule repositoryLayer_doesNotDependOnServiceOrUi =
      noClasses()
          .that()
          .resideInAPackage(BASE + ".repository..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(BASE + ".service..", BASE + ".ui..");

  @ArchTest
  static final ArchRule serviceLayer_doesNotDependOnUi =
      noClasses()
          .that()
          .resideInAPackage(BASE + ".service..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage(BASE + ".ui..");

  /**
   * Services may depend on repository interfaces but must not reference any Json* concrete
   * implementation directly — enforces coding against abstractions.
   */
  @ArchTest
  static final ArchRule serviceLayer_doesNotDependOnConcreteRepositoryImplementations =
      noClasses()
          .that()
          .resideInAPackage(BASE + ".service..")
          .should()
          .dependOnClassesThat()
          .haveSimpleNameStartingWith("Json");

  @ArchTest
  static final ArchRule uiLayer_doesNotDependOnRepository =
      noClasses()
          .that()
          .resideInAPackage(BASE + ".ui..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage(BASE + ".repository..");

  // ── Naming and annotation conventions ────────────────────────────────────

  @ArchTest
  static final ArchRule concreteControllers_areAnnotatedWithController =
      classes()
          .that()
          .resideInAPackage(BASE + ".ui..")
          .and()
          .haveSimpleNameEndingWith("Controller")
          .and(NOT_ABSTRACT)
          .should()
          .beAnnotatedWith(Controller.class);

  @ArchTest
  static final ArchRule jsonRepositories_areAnnotatedWithRepository =
      classes()
          .that()
          .resideInAPackage(BASE + ".repository..")
          .and()
          .haveSimpleNameStartingWith("Json")
          .and()
          .haveSimpleNameNotEndingWith("Test")
          .should()
          .beAnnotatedWith(Repository.class);

  @ArchTest
  static final ArchRule serviceImplementations_areAnnotatedWithService =
      classes()
          .that()
          .resideInAPackage(BASE + ".service..")
          .and()
          .haveSimpleNameEndingWith("Impl")
          .should()
          .beAnnotatedWith(Service.class);

  // ── Cyclic dependency check ───────────────────────────────────────────────

  @ArchTest
  static final ArchRule packageSlices_areFreeOfCycles =
      slices().matching(BASE + ".(*)..").should().beFreeOfCycles();
}
