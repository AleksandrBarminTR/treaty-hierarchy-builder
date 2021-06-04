package com.thomsonreuters.treaties.hierarchy.builder.extractor;

import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

@Component
public class CelexExtractor implements MetadataExtractor {
  private XPath celexExpression;

  @PostConstruct
  public void init() {
    celexExpression = XPathFactory.newInstance().newXPath();
  }

  @Override
  @SneakyThrows
  public void extract(Document document, NoticeMetadata metadata) {
    final XPathExpression compile = celexExpression.compile("//NOTICE/WORK/SAMEAS/URI[./TYPE/text() = 'celex']/IDENTIFIER/text()");
    final String identifier = (String) compile.evaluate(document, XPathConstants.STRING);

    metadata.setCelex(identifier);
  }
}
