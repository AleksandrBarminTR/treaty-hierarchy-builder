package com.thomsonreuters.treaties.hierarchy.builder.rule.engine;

import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import com.thomsonreuters.treaties.hierarchy.builder.model.PathItem;
import com.thomsonreuters.treaties.hierarchy.builder.rule.engine.Rule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Order(1)
@Component
public class EliRule implements Rule {
  @Override
  public boolean canApply(NoticeMetadata metadata) {
    return StringUtils.isNoneBlank(metadata.getEli());
  }

  @Override
  public Collection<PathItem> apply(NoticeMetadata metadata) {
    return Arrays.stream(metadata.getEli().split(":"))
        .skip(2)
        .filter(value -> !StringUtils.equalsIgnoreCase(value, "sign"))
        .filter(value -> !StringUtils.equalsIgnoreCase(value, "oj"))
        .filter(value -> StringUtils.isNoneBlank(value))
        .map(this::toPathItem)
        .collect(Collectors.toList());
  }

  private PathItem toPathItem(String item) {
    if (StringUtils.contains(item, "_")) {
      return new PathItem(
          StringUtils.substringBefore(item, "_"),
          StringUtils.substringAfter(item, "_")
      );
    }

    return new PathItem(
        "unknown-element",
        item
    );
  }
}
