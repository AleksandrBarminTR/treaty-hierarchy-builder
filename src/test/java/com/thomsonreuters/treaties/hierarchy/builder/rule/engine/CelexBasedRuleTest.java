package com.thomsonreuters.treaties.hierarchy.builder.rule.engine;

import com.thomsonreuters.treaties.hierarchy.builder.extractor.CelexExtractor;
import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import com.thomsonreuters.treaties.hierarchy.builder.model.PathItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CelexBasedRuleTest {
  @InjectMocks
  private TestCelexBasedRule rule;

  @ParameterizedTest
  @CsvSource({
      "12016M1234,true",
      "12016M12/34/56,false"
  })
  void apply_checkCelexByPattern(String celex, boolean shouldApply) {
    final NoticeMetadata metadata = new NoticeMetadata();
    metadata.setCelex(celex);

    final boolean apply = rule.canApply(metadata);

    assertEquals(shouldApply, apply);
  }

  static class TestCelexBasedRule extends CelexBasedRule {
    @Override
    public Pattern getCelexPattern() {
      return Pattern.compile("^1\\d{4}[a-zA-Z]\\d+$");
    }

    @Override
    public Collection<PathItem> apply(String celex) {
      return null;
    }
  }
}