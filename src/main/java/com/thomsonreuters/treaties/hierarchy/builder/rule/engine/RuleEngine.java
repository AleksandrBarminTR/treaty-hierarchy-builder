package com.thomsonreuters.treaties.hierarchy.builder.rule.engine;

import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import com.thomsonreuters.treaties.hierarchy.builder.model.PathItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class RuleEngine {
  @Autowired
  private Collection<Rule> rules;

  public Collection<PathItem> process(final NoticeMetadata metadata) {
    return rules.stream()
        .filter(rule -> rule.canApply(metadata))
        .findFirst()
        .map(rule -> rule.apply(metadata))
        .orElse(List.of());
  }
}
