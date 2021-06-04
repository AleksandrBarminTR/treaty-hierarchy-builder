package com.thomsonreuters.treaties.hierarchy.builder;

import com.google.common.collect.Iterables;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

@Component
public class CollectionUtils {
  public <T> boolean isLast(Iterable<T> collection, T item) {
    if (Iterables.isEmpty(collection)) {
      return false;
    }
    final T lastOne = Iterables.getLast(collection);
    return lastOne == item;
  }
}
