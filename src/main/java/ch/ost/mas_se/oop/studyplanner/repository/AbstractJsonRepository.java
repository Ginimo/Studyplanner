package ch.ost.mas_se.oop.studyplanner.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

abstract class AbstractJsonRepository<T> {

  protected final ObjectMapper mapper;

  protected AbstractJsonRepository(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  protected abstract Path filePath();

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
