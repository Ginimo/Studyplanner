package ch.ost.mas_se.oop.studyplanner.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;

abstract class AbstractJsonRepository<T> {

  protected final ObjectMapper mapper;

  @Value("${studyplanner.data.dir}")
  private Path dataDir;

  protected AbstractJsonRepository(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  protected abstract String fileName();

  protected Path filePath() {
    return dataDir.resolve(fileName());
  }

  protected abstract TypeReference<T> typeRef();

  protected abstract T emptyValue();

  protected T load() {
    if (!Files.exists(filePath())) return emptyValue();
    try {
      return mapper.readValue(filePath().toFile(), typeRef());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected void store(T data) {
    try {
      Files.createDirectories(filePath().getParent());
      mapper.writeValue(filePath().toFile(), data);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
