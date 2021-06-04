package com.thomsonreuters.treaties.hierarchy.builder;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Path;

public class TestUtils {
  @SneakyThrows
  public static Path getPathForClassPathResource(String resource) {
    return new ClassPathResource(resource).getFile().toPath();
  }
}
