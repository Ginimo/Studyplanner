package ch.ost.mas_se.oop.studyplanner;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.Schedule;
import ch.ost.mas_se.oop.studyplanner.model.Semester;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import ch.ost.mas_se.oop.studyplanner.serialization.DegreeProgramMixin;
import ch.ost.mas_se.oop.studyplanner.serialization.ScheduleMixin;
import ch.ost.mas_se.oop.studyplanner.serialization.SemesterMixin;
import ch.ost.mas_se.oop.studyplanner.serialization.StudyModuleMixin;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JacksonConfig {

  @Bean
  Jackson2ObjectMapperBuilderCustomizer registerMixins() {
    return builder ->
        builder
            .mixIn(DegreeProgram.class, DegreeProgramMixin.class)
            .mixIn(StudyModule.class, StudyModuleMixin.class)
            .mixIn(Schedule.class, ScheduleMixin.class)
            .mixIn(Semester.class, SemesterMixin.class);
  }
}
