package com.thomsonreuters.treaties.hierarchy.builder.model;

import lombok.Data;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Hierarchy of the particular document. Surprisingly, it has a lot of logic inside.
 */
@Value
public class Hierarchy {
  Element root;

  public Hierarchy(String celex) {
    root = new Element("eu-text", "000", celex, "");
  }

  public Element addElement(Element parent, String type, String number, String celex, String title) {
    return parent.addChild(new Element(
        type,
        number,
        celex,
        title
    ));
  }

  public Element addArticle(Element parent, String number, String celex, String title) {
    return addElement(getEnactingTerms(parent), "eu-article", number, celex, title);
  }

  public Element addPreamble(Element parent, String number, String celex, String title) {
    return addElement(parent, "eu-preamble", number, celex, title);
  }

  public Element addAnnex(Element parent, String number, String celex, String title) {
    return addElement(getAnnexes(parent), "eu-annex", number, celex, title);
  }

  public Element addProtocol(Element parent, String number, String celex, String title) {
    return addElement(getProtocols(parent), "eu-protocol", number, celex, title);
  }

  public Element addFinal(Element parent, String number, String celex, String title) {
    return addElement(parent, "eu-final", number, celex, title);
  }

  public Element addAct(Element parent, String number, String celex, String title) {
    return addElement(getActs(parent), "eu-act", number, celex, title);
  }

  public Element addDeclaration(Element parent, String number, String celex, String title) {
    return addElement(getDeclarations(parent), "eu-fake-declaration", number, celex, title);
  }

  public Element addUnknown(Element parent, String celex, String title) {
    final Element unknownsParent = getUnknowns(parent);
    return addElement(
        unknownsParent,
        "eu-unknown",
        StringUtils.leftPad(String.valueOf(unknownsParent.getChildren().size() + 1), 3, "0"),
        celex,
        title
    );
  }

  private Element getUnknowns(Element parent) {
    return getFirstChild(parent, "eu-unknowns")
        .orElseGet(() -> {
          return parent.addChild(new Element(
              "eu-unknowns",
              "600",
              parent.getCelex(),
              parent.getTitle()
          ));
        });
  }

  private Element getDeclarations(Element parent) {
    return getFirstChild(parent, "eu-declarations")
        .orElseGet(() -> {
          return parent.addChild(new Element(
              "eu-declarations",
              "500",
              parent.getCelex(),
              parent.getTitle()
          ));
        });
  }

  private Element getActs(Element parent) {
    return getFirstChild(parent, "eu-acts")
        .orElseGet(() -> {
          return parent.addChild(new Element(
              "eu-acts",
              "400",
              parent.getCelex(),
              parent.getTitle()
          ));
        });
  }

  private Element getProtocols(Element parent) {
    return getFirstChild(parent, "eu-protocols")
        .orElseGet(() -> {
          return parent.addChild(new Element(
              "eu-protocols",
              "300",
              parent.getCelex(),
              parent.getTitle()
          ));
        });
  }

  private Element getAnnexes(Element parent) {
    return getFirstChild(parent, "eu-annexes")
        .orElseGet(() -> {
          return parent.addChild(new Element(
              "eu-annexes",
              "200",
              parent.getCelex(),
              parent.getTitle()
          ));
        });
  }

  private Element getEnactingTerms(Element parent) {
    return getFirstChild(parent, "eu-enacting-terms")
        .orElseGet(() -> {
          return parent.addChild(new Element(
              "eu-enacting-terms",
              "100",
              parent.getCelex(),
              parent.getTitle()
          ));
        });
  }

  private Optional<Element> getFirstChild(Element parent, String type) {
    return parent.getChildren()
        .stream()
        .filter(child -> StringUtils.equalsIgnoreCase(child.getType(), type))
        .findFirst();
  }
}
