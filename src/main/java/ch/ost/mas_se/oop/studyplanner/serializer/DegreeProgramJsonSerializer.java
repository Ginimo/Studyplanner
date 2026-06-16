package ch.ost.mas_se.oop.studyplanner.serializer;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class DegreeProgramJsonSerializer extends JsonSerializer<DegreeProgram> {

  @Override
  public void serialize(
      DegreeProgram degreeProgram,
      JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider)
      throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField("id", degreeProgram.getId());
    jsonGenerator.writeStringField("name", degreeProgram.getName());
    jsonGenerator.writeStringField("description", degreeProgram.getDescription());
    jsonGenerator.writeNumberField(
        "maxDurationInSemesters", degreeProgram.getMaxDurationInSemesters());
    jsonGenerator.writeEndObject();
  }
}
