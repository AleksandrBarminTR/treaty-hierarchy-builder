package com.thomsonreuters.treaties.hierarchy.builder.extractor;

import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import com.thomsonreuters.treaties.hierarchy.builder.model.PathItem;
import com.thomsonreuters.treaties.hierarchy.builder.rule.engine.EliRule;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EliRuleTest {
  @InjectMocks
  private EliRule rule;

  @ParameterizedTest
  @CsvSource({
      "treaty:fusion:art_14:sign,true",
      ",false"
  })
  void canApply_applyWhenEliIsProvided(String eli, boolean canApply) {
    final NoticeMetadata metadata = new NoticeMetadata();
    metadata.setEli(eli);

    final boolean apply = rule.canApply(metadata);

    assertEquals(canApply, apply);
  }

  @ParameterizedTest
  @CsvSource({
      "treaty:fusion:art_14:sign,art_14",
      "treaty:fusion:art_1:art_2:oj,art_1:art_2"
  })
  void apply_shouldExtractPath(String eli, String expected) {
    final NoticeMetadata metadata = new NoticeMetadata();
    metadata.setEli(eli);

    final Collection<PathItem> result = rule.apply(metadata);

    assertNotNull(result);

    final List<PathItem> expectedList = Arrays.stream(expected.split(":"))
        .map(line -> new PathItem(
            StringUtils.substringBefore(line, "_"),
            StringUtils.substringAfter(line, "_")
        ))
        .collect(Collectors.toList());

    assertIterableEquals(expectedList, result);
  }
}