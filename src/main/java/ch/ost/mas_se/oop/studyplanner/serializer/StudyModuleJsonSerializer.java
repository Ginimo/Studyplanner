package ch.ost.mas_se.oop.studyplanner.serializer;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;

public class StudyModuleJsonSerializer extends JsonSerializer<StudyModule> {

  @Override
  public void serialize(
      StudyModule module, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
      throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField("studyModuleKey", module.getStudyModuleKey());
    jsonGenerator.writeStringField("programId", module.getDegreeProgramId());
    jsonGenerator.writeStringField("name", module.getName());
    jsonGenerator.writeStringField("description", module.getDescription());
    jsonGenerator.writeNumberField("ects", module.getEcts());
    jsonGenerator.writeArrayFieldStart("prerequisiteKeys");
    for (var key : module.getPrerequisiteKeys()) {
      jsonGenerator.writeString(key);
    }
    jsonGenerator.writeEndArray();

    if (module.getOfferedFrom() != null) {
      jsonGenerator.writeStringField("offeredFrom", module.getOfferedFrom().toString());
    } else {
      jsonGenerator.writeNullField("offeredFrom");
    }

    if (module.getOfferedTo() != null) {
      jsonGenerator.writeStringField("offeredTo", module.getOfferedTo().toString());
    } else {
      jsonGenerator.writeNullField("offeredTo");
    }
    jsonGenerator.writeEndObject();
  }
}
