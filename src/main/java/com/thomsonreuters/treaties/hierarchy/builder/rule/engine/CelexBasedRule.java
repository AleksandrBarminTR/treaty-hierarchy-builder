package com.thomsonreuters.treaties.hierarchy.builder.rule.engine;

import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import com.thomsonreuters.treaties.hierarchy.builder.model.PathItem;

import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CelexBasedRule implements Rule {
  @Override
  public boolean canApply(NoticeMetadata metadata) {
    return getMatching(getCelexPatterns(), metadata.getCelex())
        .isPresent();
  }

  @Override
  public Collection<PathItem> apply(NoticeMetadata metadata) {
    final Optional<Matcher> optionalMatcher = getMatching(getCelexPatterns(), metadata.getCelex());
    if (optionalMatcher.isEmpty()) {
      throw new IllegalArgumentException(String.format(
          "Provided metadata can't be processed with this Rule, have you invoked canApply before?"
      ));
    }
    final Matcher matcher = optionalMatcher.get();
    return apply(matcher);
  }

  private Optional<Matcher> getMatching(Collection<Pattern> patterns, String celex) {
    return patterns.stream()
        .map(pattern -> pattern.matcher(celex))
        .filter(matcher -> matcher.matches())
        .findFirst();
  }

  /**
   * Required celex pattern should be returned.
   * @return
   */
  public abstract Collection<Pattern> getCelexPatterns();

  public abstract Collection<PathItem> apply(Matcher matcher);

  protected String normalizeNumber(Matcher matcher, String group) {
    final String intString = matcher.group(group);
    if (isInteger(intString)) {
      final int intValue = Integer.parseInt(intString);
      return String.valueOf(intValue);
    }
    return intString;
  }

  private boolean isInteger(String candidate) {
    try {
      Integer.parseInt(candidate);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
