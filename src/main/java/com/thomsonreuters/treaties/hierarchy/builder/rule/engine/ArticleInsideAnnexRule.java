package com.thomsonreuters.treaties.hierarchy.builder.rule.engine;

import com.thomsonreuters.treaties.hierarchy.builder.model.PathItem;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This rule for celex numbers like 11979HN01/16.
 * In this case N01 is the first annex, 16 is an article inside the annex.
 */
@Component
public class ArticleInsideAnnexRule extends CelexBasedRule {
  @Override
  public Collection<Pattern> getCelexPatterns() {
    return List.of(
        Pattern.compile("^1\\d{4}[a-zA-Z]{2}(?<annex>\\d+)/(?<article>[\\da-zA-Z]+)$")
    );
  }

  @Override
  public Collection<PathItem> apply(Matcher matcher) {
    return List.of(
        new PathItem("anx", normalizeNumber(matcher, "annex")),
        new PathItem("art", normalizeNumber(matcher, "article"))
    );
  }
}
