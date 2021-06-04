package com.thomsonreuters.treaties.hierarchy.builder.rule.engine;

import com.google.common.collect.Iterables;
import com.thomsonreuters.treaties.hierarchy.builder.CollectionUtils;
import com.thomsonreuters.treaties.hierarchy.builder.NoticePathProvider;
import com.thomsonreuters.treaties.hierarchy.builder.extractor.CelexExtractor;
import com.thomsonreuters.treaties.hierarchy.builder.extractor.NoticeMetadataProvider;
import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import com.thomsonreuters.treaties.hierarchy.builder.model.PathItem;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectionWithLetterRuleTest {
  @Mock
  private CelexExtractor celexExtractor;

  @Mock
  private RuleEngine ruleEngine;

  @Mock
  private NoticePathProvider pathProvider;

  @Mock
  private NoticeMetadataProvider metadataProvider;

  @Spy
  private CollectionUtils collectionUtils;

  @InjectMocks
  private SectionWithLetterRule rule;

  @ParameterizedTest
  @CsvSource({
      "12016M0123A,true",
      "12016M01234,false"
  })
  void canApply_appliedOnlyToCelexNumbersWithLetters(String celex, boolean shouldApply) {
    final NoticeMetadata metadata = new NoticeMetadata();
    metadata.setCelex(celex);

    final boolean canApply = rule.canApply(metadata);

    assertEquals(shouldApply, canApply);
  }

  @ParameterizedTest
  @CsvSource({
      "12016M01234A,12016M01234,art_1,art_1A",
      "12016M01234PRIME,12016M01234,art_1,art_1PRIME"
  })
  void apply_shouldExtractPath(String currentCelex, String mainCelex, String mainPath, String expectedPath) {
    final NoticeMetadata mainMetadata = mock(NoticeMetadata.class);
    when(pathProvider.getNoticePathByCelex(mainCelex)).thenReturn(Optional.of(Path.of(mainCelex)));
    when(metadataProvider.provide(eq(Path.of(mainCelex)))).thenReturn(mainMetadata);
    when(ruleEngine.process(eq(mainMetadata))).thenReturn(List.of(new PathItem(
        StringUtils.substringBefore(mainPath, "_"),
        StringUtils.substringAfter(mainPath, "_")
    )));

    final Collection<PathItem> path = rule.apply(currentCelex);

    assertNotNull(path);
    assertFalse(path.isEmpty());
    assertEquals(1, path.size());

    final PathItem lastPath = Iterables.getLast(path);

    assertEquals(
        StringUtils.substringBefore(expectedPath, "_"),
        lastPath.getElement()
    );
    assertEquals(
        StringUtils.substringAfter(expectedPath, "_"),
        lastPath.getNumber()
    );
  }
}