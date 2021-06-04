package com.thomsonreuters.treaties.hierarchy.builder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Slf4j
@Component
public class HierarchyBuildingRunner implements ApplicationRunner {
  @Value("${source-folder}")
  private String sourceFolder;

  @Value("${target-folder}")
  private String targetFolder;

  @Autowired
  private HierarchySaver saver;

  @Autowired
  private HierarchyBuilder builder;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    final Path docsFolder = Path.of(sourceFolder);
    if (!Files.exists(docsFolder)) {
      log.error("The source folder doesn't exist");
      return;
    }

    Arrays.stream(docsFolder.toFile().listFiles())
        .filter(file -> file.isDirectory())
//        .limit(1) // only one doc at once
        .map(File::toPath)
        .filter(this::toProcess)
        .map(builder::build)
        .forEach(saver::save);
  }

  private boolean toProcess(Path folderPath) {
    final Path resultFile = Path.of(targetFolder)
        .resolve(folderPath.getFileName() + ".xml");
    return !Files.exists(resultFile);
  }
}
