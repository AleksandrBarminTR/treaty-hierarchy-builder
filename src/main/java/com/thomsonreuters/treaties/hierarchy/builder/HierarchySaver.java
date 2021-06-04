package com.thomsonreuters.treaties.hierarchy.builder;

import com.thomsonreuters.treaties.hierarchy.builder.model.Element;
import com.thomsonreuters.treaties.hierarchy.builder.model.Hierarchy;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Component
public class HierarchySaver {
  @Value("${target-folder}")
  private String targetFolder;

  private Transformer prettyTransformer;

  @PostConstruct
  public void init() throws Exception {
    final Path target = Path.of(targetFolder);
    if (!Files.exists(target)) {
      Files.createDirectories(target);
    }

    prettyTransformer = TransformerFactory.newInstance().newTransformer();
    prettyTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
  }

  @SneakyThrows
  public void save(Hierarchy hierarchy) {
    final Path tempFile = Path.of(targetFolder)
        .resolve(hierarchy.getRoot().getCelex() + "_tmp.xml");

    final Path targetFile = Path.of(targetFolder)
        .resolve(hierarchy.getRoot().getCelex() + ".xml");

    Files.deleteIfExists(tempFile);
    Files.createFile(tempFile);

    Files.deleteIfExists(targetFile);
    Files.createFile(targetFile);

    try (final BufferedWriter targetStream = Files.newBufferedWriter(
        tempFile,
        StandardCharsets.UTF_8,
        StandardOpenOption.WRITE
    )) {
      targetStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      write(targetStream, hierarchy.getRoot());
    }

    // XML pretty print
    @Cleanup final InputStream inputStream = Files.newInputStream(tempFile, StandardOpenOption.READ);
    @Cleanup final OutputStream outputStream = Files.newOutputStream(targetFile, StandardOpenOption.WRITE);
    prettyTransformer.transform(
        new StreamSource(inputStream),
        new StreamResult(outputStream)
    );

    Files.deleteIfExists(tempFile);
  }

  @SneakyThrows
  private void write(BufferedWriter targetStream, Element element) {
    targetStream.write("<" + element.getType() + " ");
    targetStream.write("celex=\"" + element.getCelex() + "\" ");
    if (StringUtils.isNoneEmpty(element.getTitle())) {
      targetStream.write("title=\"" + element.getTitle() + "\"");
    }
    if (element.getChildren().isEmpty()) {
      targetStream.write("/>");
      return;
    } else {
      targetStream.write(">");
    }
    element.getChildren().forEach(child -> write(targetStream, child));
    targetStream.write("</" + element.getType() + ">");
  }
}
