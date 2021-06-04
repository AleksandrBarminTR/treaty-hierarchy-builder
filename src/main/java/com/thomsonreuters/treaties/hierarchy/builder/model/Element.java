package com.thomsonreuters.treaties.hierarchy.builder.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

@Value
public class Element implements Comparable<Element> {
  String type;
  String number;
  String celex;
  String title;

  Collection<Element> children = Sets.newTreeSet();

  public Element addChild(Element element) {
    // first, it's necessary checking if an element with the given name exists
    for (Element child : children) {
      if (
          StringUtils.equalsIgnoreCase(child.getType(), element.getType()) &&
          StringUtils.equalsIgnoreCase(child.getNumber(), element.getNumber())
      ) {
        return child;
      }
    }
    children.add(element);
    return element;
  }

  @Override
  public int compareTo(Element o) {
    return String.CASE_INSENSITIVE_ORDER.compare(
        StringUtils.leftPad(getNumber(), 10, "0"),
        StringUtils.leftPad(o.getNumber(), 10, "0")
    );
  }
}
