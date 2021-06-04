package com.thomsonreuters.treaties.hierarchy.builder.rule.engine;

import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import com.thomsonreuters.treaties.hierarchy.builder.model.PathItem;

import java.util.Collection;

public interface Rule {
  boolean canApply(NoticeMetadata metadata);

  Collection<PathItem> apply(NoticeMetadata metadata);
}
