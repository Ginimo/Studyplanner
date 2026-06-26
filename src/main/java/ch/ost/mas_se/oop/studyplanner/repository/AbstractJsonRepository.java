package ch.ost.mas_se.oop.studyplanner.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

abstract class AbstractJsonRepository<T> {

  private static final Logger log = LoggerFactory.getLogger(AbstractJsonRepository.class);

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
    Path path = filePath();
    if (!Files.exists(path)) return emptyValue();
    try {
      return mapper.readValue(path.toFile(), typeRef());
    } catch (IOException e) {
      log.error("Failed to read {}", path, e);
      throw new RuntimeException("Failed to read " + path, e);
    }
  }

  protected void store(T data) {
    Path path = filePath();
    try {
      Files.createDirectories(path.getParent());
      mapper.writeValue(path.toFile(), data);
    } catch (IOException e) {
      log.error("Failed to write {}", path, e);
      throw new RuntimeException("Failed to write " + path, e);
    }
  }
}
