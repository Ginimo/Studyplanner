package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.StudyModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JsonStudyModuleRepository implements StudyModuleRepository {

  private final ObjectMapper mapper = new ObjectMapper();
  private final String file_path_str = "data/study_modules.json";
  private final File file = new File(file_path_str);

  private String storeKey(String degreeProgramId, String moduleKey) {
    return degreeProgramId + "#" + moduleKey;
  }

  @Override
  public void save(StudyModule module) {
    try {
      String jsonString = mapper.writeValueAsString(module);
      Optional<StudyModule> existing_module =
          findByKey(module.getDegreeProgramId(), module.getStudyModuleKey());
      if (existing_module.isEmpty()) {
        try (var writer = new PrintWriter(new FileWriter(file, true))) {
          writer.println(jsonString);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Fehler beim Speichern", e);
    }
  }

  @Override
  public Optional<StudyModule> findByKey(String degreeProgramId, String key) {
    try {
      var lines = Files.readAllLines(Path.of(file_path_str));
      for (var line : lines) {
        var module = mapper.readValue(line, StudyModule.class);
        if (degreeProgramId.equals(module.getDegreeProgramId())
            && key.equals(module.getStudyModuleKey())) {
          return Optional.of(module);
        }
      }
      return Optional.empty(); // alle Zeilen durch und nichts gefunden
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public List<StudyModule> findAllByDegreeProgramId(String degreeProgramId) {
    try {
      var lines = Files.readAllLines(Path.of(file_path_str));
      var modules = new ArrayList<StudyModule>();
      for (var line : lines) {
        var module = mapper.readValue(line, StudyModule.class);
        if (module.getDegreeProgramId().equals(degreeProgramId)) {
          modules.add(module);
        }
      }
      return modules;
    } catch (Exception e) {
      return new ArrayList<>();
    }
  }

  public void update(StudyModule module) {
    try {
      var lines = Files.readAllLines(Path.of(file_path_str));
      try (var writer = new PrintWriter(new FileWriter(file, false))) { // false = überschreiben
        for (var line : lines) {
          var existing = mapper.readValue(line, StudyModule.class);
          if (module.getDegreeProgramId().equals(existing.getDegreeProgramId())
              && module.getStudyModuleKey().equals(existing.getStudyModuleKey())) {
            writer.println(mapper.writeValueAsString(module)); // Update Version schreiben
          } else {
            writer.println(line); // Bisherige Version schreiben
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Fehler beim Updaten", e);
    }
  }

  @Override
  public void delete(String degreeProgramId, String key) {
    try {
      var lines = Files.readAllLines(Path.of(file_path_str));
      try (var writer = new PrintWriter(new FileWriter(file, false))) { // false = überschreiben
        for (var line : lines) {
          var program = mapper.readValue(line, StudyModule.class);

          if (!(degreeProgramId.equals(program.getDegreeProgramId())
              && key.equals(program.getStudyModuleKey()))) {
            writer.println(line);
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Fehler beim Löschen", e);
    }
  }
}
