package com.thomsonreuters.treaties.hierarchy.builder;

import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import com.thomsonreuters.treaties.hierarchy.builder.model.PathItem;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class TestUtils {
  @SneakyThrows
  public static Path getPathForClassPathResource(String resource) {
    return new ClassPathResource(resource).getFile().toPath();
  }

  public static Collection<PathItem> toPath(String pathString) {
    return Arrays.stream(pathString.split(":"))
        .map(line -> new PathItem(
            StringUtils.substringBefore(line, "_"),
            StringUtils.substringAfter(line, "_")
        ))
        .collect(Collectors.toList());
  }

  public static NoticeMetadata withCelex(String celex) {
    final NoticeMetadata metadata = new NoticeMetadata();
    metadata.setCelex(celex);
    return metadata;
  }
}
