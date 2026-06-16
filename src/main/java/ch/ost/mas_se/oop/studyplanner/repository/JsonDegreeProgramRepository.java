package ch.ost.mas_se.oop.studyplanner.repository;

import ch.ost.mas_se.oop.studyplanner.model.DegreeProgram;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class JsonDegreeProgramRepository implements DegreeProgramRepository {

  private final ObjectMapper mapper = new ObjectMapper();
  private final String file_path_str = "data/degree_programs.json";
  private final File file = new File(file_path_str);

  @Override
  public void save(DegreeProgram program) {
    try {
      String jsonString = mapper.writeValueAsString(program); // transitive Serialisierung
      Optional<DegreeProgram> existing_degree_program = findById(program.getId());
      if (existing_degree_program.isEmpty()) {
        try (var writer =
            new PrintWriter(
                new FileWriter(
                    file, true))) { // {try-with-resources, da drin der JSON Serizailer, da drin der
          // FileWriter mit append Option
          writer.println(jsonString);
        }
      }
    } catch (Exception e) {

      throw new RuntimeException("Fehler beim Speichern", e);
    }
  }

  @Override
  public Optional<DegreeProgram> findById(String id) {
    try {
      var lines = Files.readAllLines(Path.of(file_path_str));
      for (var line : lines) {
        var program = mapper.readValue(line, DegreeProgram.class);
        if (id.equals(program.getId())) {
          return Optional.of(program);
        }
      }
      return Optional.empty(); // alle Zeilen durch und nichts gefunden
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public ArrayList<DegreeProgram> findAll() {
    try {
      var lines = Files.readAllLines(Path.of(file_path_str)); // JSON String komplett einlesen
      var degreePrograms = new ArrayList<DegreeProgram>();
      for (var line : lines) {
        // IO.println(line); for debugging
        var program =
            mapper.readValue(
                line,
                DegreeProgram
                    .class); // Mapper Function von JSON Jackson. Benötigt Serializer um ein neues
        // Objekt zu erzeugen.
        degreePrograms.add(program);
      }
      return degreePrograms;
    } catch (Exception e) {
      return new ArrayList<>();
    }
  }

  @Override
  public void delete(String id) {
    try {
      var lines = Files.readAllLines(Path.of(file_path_str));
      try (var writer = new PrintWriter(new FileWriter(file, false))) { // false = überschreiben
        for (var line : lines) {
          var program = mapper.readValue(line, DegreeProgram.class);
          if (!id.equals(
              program.getId())) { // alle schreiben in json, ausser die gelöscht  werden soll
            writer.println(line);
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Fehler beim Löschen", e);
    }
  }

  @Override
  public void update(DegreeProgram program) {
    try {
      var lines = Files.readAllLines(Path.of(file_path_str));
      try (var writer = new PrintWriter(new FileWriter(file, false))) { // false = überschreiben
        for (var line : lines) {
          var existing = mapper.readValue(line, DegreeProgram.class);
          if (program.getId().equals(existing.getId())) {
            writer.println(mapper.writeValueAsString(program)); // Update Version schreiben
          } else {
            writer.println(line); // Bisherige Version schreiben
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Fehler beim Updaten", e);
    }
  }
}
