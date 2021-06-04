package com.thomsonreuters.treaties.hierarchy.builder.rule.engine;

import com.google.common.collect.Lists;
import com.thomsonreuters.treaties.hierarchy.builder.CollectionUtils;
import com.thomsonreuters.treaties.hierarchy.builder.NoticePathProvider;
import com.thomsonreuters.treaties.hierarchy.builder.extractor.NoticeMetadataProvider;
import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import com.thomsonreuters.treaties.hierarchy.builder.model.PathItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SectionWithLetterRule extends CelexBasedRule {
  private final Pattern CELEX_SUB_ARTICLE_PATTERN = Pattern.compile("(1\\d{4}[a-zA-Z]\\d+)([a-zA-Z]+)");

  @Autowired
  private RuleEngine engine;

  @Autowired
  private NoticeMetadataProvider metadataProvider;

  @Autowired
  private NoticePathProvider noticePathProvider;

  @Autowired
  private CollectionUtils collectionUtils;

  @Override
  public Pattern getCelexPattern() {
    return CELEX_SUB_ARTICLE_PATTERN;
  }

  @Override
  public Collection<PathItem> apply(String celex) {
    final Matcher matcher = CELEX_SUB_ARTICLE_PATTERN.matcher(celex);
    matcher.find();

    final String mainCelex = matcher.group(1);
    final String subPart = matcher.group(2);

    // get metadata for the base notice
    final NoticeMetadata parentMetadata = noticePathProvider.getNoticePathByCelex(mainCelex)
        .map(metadataProvider::provide)
        .orElse(new NoticeMetadata());

    // extract path from it
    final Collection<PathItem> parentPath = engine.process(parentMetadata);

    if (parentPath.isEmpty()) {
      // didn't manage to get anything from the parent doc
      return parentPath;
    }

    // need to take the last one path item
    final List<PathItem> newPath = Lists.newArrayList();
    for (PathItem item : parentPath) {
      if (collectionUtils.isLast(parentPath, item)) {
        // re-creating the path
        newPath.add(new PathItem(
            item.getElement(),
            item.getNumber() + subPart
        ));
      } else {
        newPath.add(item);
      }
    }

    return newPath;
  }
}
