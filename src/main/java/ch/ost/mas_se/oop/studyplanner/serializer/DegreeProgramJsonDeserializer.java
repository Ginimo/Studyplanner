package ch.ost.mas_se.oop.studyplanner.serializer;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class DegreeProgramJsonDeserializer extends JsonDeserializer<DegreeProgram> {

  @Override
  public DegreeProgram deserialize(JsonParser jsonParser, DeserializationContext dC)
      throws IOException {
    var tree = jsonParser.readValueAs(JsonNode.class);
    var id = tree.get("id").asText();
    var name = tree.get("name").asText();
    var description = tree.get("description").asText();
    var maxDurationInSemesters = tree.get("maxDurationInSemesters").asInt();
    return new DegreeProgram(id, name, description, maxDurationInSemesters);
  }
}
