package com.thomsonreuters.treaties.hierarchy.builder.extractor;

import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import org.w3c.dom.Document;

public interface MetadataExtractor {
  void extract(Document document, NoticeMetadata metadata);
}
