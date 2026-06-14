package ch.ost.mas_se.oop.studyplanner.serializer;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class StudyModuleJsonDeserializer extends JsonDeserializer<StudyModule> {

    @Override
    public StudyModule  deserialize (JsonParser jsonParser, DeserializationContext dC) throws IOException {
        var tree = jsonParser.readValueAs(JsonNode.class);
        var studyModuleKey = tree.get("studyModuleKey").asText();
        var programId = tree.get("programId").asText();
        var name = tree.get("name").asText();
        var description = tree.get("description").asText();
        var ects = tree.get("ects").asInt();
        var prerequisiteKeys = new ArrayList<String>();
        for (var key : tree.get("prerequisiteKeys")) {
            prerequisiteKeys.add(key.asText());
        }
        var offeredFromNode = tree.get("offeredFrom");
        var offeredToNode = tree.get("offeredTo");

        var offeredFrom = (offeredFromNode == null || offeredFromNode.isNull()) ? null : LocalDate.parse(offeredFromNode.asText());
        var offeredTo = (offeredToNode == null || offeredToNode.isNull()) ? null : LocalDate.parse(offeredToNode.asText());
        return new StudyModule(studyModuleKey, programId, name, description, ects, prerequisiteKeys, offeredFrom, offeredTo);
    }
}
