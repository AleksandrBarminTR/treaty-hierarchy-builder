package com.thomsonreuters.treaties.hierarchy.builder.extractor;

import com.thomsonreuters.treaties.hierarchy.builder.TestUtils;
import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NoticeMetadataProviderTest {
  @Mock
  private MetadataExtractor extractor;

  @Spy
  private Collection<MetadataExtractor> extractors = Lists.newArrayList();

  @InjectMocks
  private NoticeMetadataProvider provider;

  private Path noticePath = TestUtils.getPathForClassPathResource("./notice_1.xml");

  @BeforeEach
  void setUp() throws Exception {
    extractors.add(extractor);
    provider.init();
  }

  @Test
  void provide_shouldReturnNoticeObject() {
    final NoticeMetadata metadata = provider.provide(noticePath);

    assertNotNull(metadata);
  }

  @Test
  void provide_thePathShouldBeSet() {
    final NoticeMetadata metadata = provider.provide(noticePath);

    assertEquals(noticePath, metadata.getFilePath());
  }

  @Test
  void provide_extractorsShouldBeChecked() {
    final NoticeMetadata metadata = provider.provide(noticePath);

    verify(extractors, times(1)).stream();
    verify(extractor, times(1)).extract(any(Document.class), eq(metadata));
  }
}