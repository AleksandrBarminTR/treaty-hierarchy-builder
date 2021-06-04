package com.thomsonreuters.treaties.hierarchy.builder.rule.engine;

import com.thomsonreuters.treaties.hierarchy.builder.TestUtils;
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
class ParaInsideArticleInsideAnnexRuleTest {
  @InjectMocks
  private ParaInsideArticleInsideAnnexRule rule;

  @CsvSource({
      "11979HN01/02/A,true",
      "11979HN01/02,false",
      "11979NHN01,false"
  })
  @ParameterizedTest
  void canApply_checkingCelexTemplates(String celex, boolean shouldApply) {
    final boolean canApply = rule.canApply(TestUtils.withCelex(celex));

    assertEquals(shouldApply, canApply);
  }

  @CsvSource({
      "11979HN01/02/A,anx_1:art_2:par_A",
      "11979HN9999/8888/ABCD,anx_9999:art_8888:par_ABCD"
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