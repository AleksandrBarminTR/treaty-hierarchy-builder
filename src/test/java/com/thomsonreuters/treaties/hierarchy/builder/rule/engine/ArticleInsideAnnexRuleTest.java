package com.thomsonreuters.treaties.hierarchy.builder.rule.engine;

import com.thomsonreuters.treaties.hierarchy.builder.TestUtils;
import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import com.thomsonreuters.treaties.hierarchy.builder.model.PathItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ArticleInsideAnnexRuleTest {
  @InjectMocks
  private ArticleInsideAnnexRule rule;

  @CsvSource({
      "11979HN01/16,true",
      "11985IN15/A,true",
      "11985IN30/A,true",
      "11979HN01,false",
      "11979HN9999/8888,true",
      "11979HN02/01/1/AA,fase"
  })
  @ParameterizedTest
  void canApply_shouldFollowTheCelexPattern(String celex, boolean shouldApply) {
    final boolean canApply = rule.canApply(TestUtils.withCelex(celex));

    assertEquals(shouldApply, canApply);
  }

  @CsvSource({
      "11979HN01/16,anx_1:art_16",
      "11979HN9999/8888,anx_9999:art_8888",
      "11985IN15/A,anx_15:art_A",
      "11985IN30/A,anx_30:art_A",
  })
  @ParameterizedTest
  void apply_shouldExtractPathFromCelex(String celex, String expectedPath) {
    final Collection<PathItem> path = rule.apply(TestUtils.withCelex(celex));

    assertIterableEquals(
        TestUtils.toPath(expectedPath),
        path
    );
  }
}