package com.thomsonreuters.treaties.hierarchy.builder.rule.engine;

import com.thomsonreuters.treaties.hierarchy.builder.extractor.CelexExtractor;
import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import com.thomsonreuters.treaties.hierarchy.builder.model.PathItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CelexBasedRule implements Rule {
  @Override
  public boolean canApply(NoticeMetadata metadata) {
    final Matcher matcher = getCelexPattern().matcher(metadata.getCelex());
    return matcher.matches();
  }

  @Override
  public Collection<PathItem> apply(NoticeMetadata metadata) {
    return apply(metadata.getCelex());
  }

  /**
   * Required celex pattern should be returned.
   * @return
   */
  public abstract Pattern getCelexPattern();

  public abstract Collection<PathItem> apply(String celex);
}
