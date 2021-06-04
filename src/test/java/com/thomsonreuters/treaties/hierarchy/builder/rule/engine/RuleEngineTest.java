package com.thomsonreuters.treaties.hierarchy.builder.rule.engine;

import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import com.thomsonreuters.treaties.hierarchy.builder.model.PathItem;
import lombok.SneakyThrows;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Path;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleEngineTest {
  @Spy
  private Collection<Rule> rules = Lists.newArrayList();

  @Mock
  private Rule rule;

  @InjectMocks
  private RuleEngine uut;

  @Mock
  private NoticeMetadata metadata;

  @BeforeEach
  void setUp() {
    rules.add(rule);
  }

  @Test
  void process_shouldReturnCollection() {
    final Collection<PathItem> result = uut.process(metadata);

    assertNotNull(result);
    assertTrue(result instanceof Collection);
  }

  @Test
  void process_shouldCallRules() {
    when(rule.canApply(any(NoticeMetadata.class))).thenReturn(true);
    uut.process(metadata);

    verify(rules, times(1)).stream();

    final InOrder inOrder = inOrder(rule);

    inOrder.verify(rule, times(1)).canApply(eq(metadata));
    inOrder.verify(rule, times(1)).apply(eq(metadata));
  }
}