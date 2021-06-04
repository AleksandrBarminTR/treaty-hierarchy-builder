package com.thomsonreuters.treaties.hierarchy.builder.rule.engine;

import com.thomsonreuters.treaties.hierarchy.builder.model.PathItem;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 11979HN01/02/A - annex 01, article 02, para A
 */
@Component
public class ParaInsideArticleInsideAnnexRule extends CelexBasedRule {
  @Override
  public Collection<Pattern> getCelexPatterns() {
    return List.of(
        Pattern.compile("^1\\d{4}[a-zA-Z]{2}(?<annex>\\d+)/(?<article>\\d+)/(?<para>.+)$")
    );
  }

  @Override
  public Collection<PathItem> apply(Matcher matcher) {
    return List.of(
        new PathItem("anx", normalizeNumber(matcher, "annex")),
        new PathItem("art", normalizeNumber(matcher, "article")),
        new PathItem("par", matcher.group("para"))
    );
  }
}
