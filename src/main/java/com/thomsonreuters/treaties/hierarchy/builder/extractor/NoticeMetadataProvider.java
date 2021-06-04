package com.thomsonreuters.treaties.hierarchy.builder.extractor;

import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.util.Collection;

@Component
public class NoticeMetadataProvider {
  @Autowired
  private Collection<MetadataExtractor> extractors;

  private DocumentBuilder documentBuilder;

  @PostConstruct
  public void init() throws Exception {
    documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
  }

  @SneakyThrows
  public NoticeMetadata provide(final Path noticePath) {
    final NoticeMetadata metadata = new NoticeMetadata();
    metadata.setFilePath(noticePath);

    final Document document = documentBuilder.parse(noticePath.toFile());

    extractors.stream()
        .forEach(extractor -> extractor.extract(document, metadata));

    return metadata;
  }
}
