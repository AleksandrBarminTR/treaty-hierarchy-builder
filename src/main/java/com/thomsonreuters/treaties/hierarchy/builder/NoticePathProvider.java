package com.thomsonreuters.treaties.hierarchy.builder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Component
public class NoticePathProvider {
  @Value("${source-folder}")
  private String sourceFolder;

  public Optional<Path> getNoticePathByCelex(String celex) {
    final Path possiblePath = Path.of(sourceFolder)
        .resolve(StringUtils.left(celex, 6))
        .resolve(celex + "_notice.xml");

    if (Files.exists(possiblePath)) {
      return Optional.of(possiblePath);
    }

    return Optional.empty();
  }
}
