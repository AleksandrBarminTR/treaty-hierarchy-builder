package com.thomsonreuters.treaties.hierarchy.builder.extractor;

import com.thomsonreuters.treaties.hierarchy.builder.TestUtils;
import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CelexExtractorTest {
  @InjectMocks
  private CelexExtractor extractor;

  private DocumentBuilder builder;
  private Document document;
  private NoticeMetadata noticeMetadata;

  @BeforeEach
  void setUp() throws Exception {
    extractor.init();

    noticeMetadata = new NoticeMetadata();
    builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    document = builder.parse(TestUtils.getPathForClassPathResource("./notice_1.xml").toFile());
  }

  @Test
  void extract_shouldExtractCelex()  {
    extractor.extract(document, noticeMetadata);

    assertNotNull(noticeMetadata.getCelex());
    assertEquals("11965F014", noticeMetadata.getCelex());
  }
}