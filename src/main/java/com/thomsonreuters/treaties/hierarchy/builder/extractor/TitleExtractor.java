package com.thomsonreuters.treaties.hierarchy.builder.extractor;

import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

@Component
public class TitleExtractor implements MetadataExtractor {
  private XPath eliExpression;

  @PostConstruct
  public void init() throws  Exception {
    eliExpression = XPathFactory.newInstance().newXPath();
  }

  @Override
  @SneakyThrows
  public void extract(Document document, NoticeMetadata metadata) {
    final XPathExpression compile = eliExpression.compile("//NOTICE/WORK/WORK_HAS_EXPRESSION/EMBEDDED_NOTICE/EXPRESSION[./EXPRESSION_USES_LANGUAGE/IDENTIFIER/text() = 'ENG']/EXPRESSION_TITLE/VALUE/text()");
    String identifier = (String) compile.evaluate(document, XPathConstants.STRING);
    identifier = StringUtils.replace(identifier, "\"", "");
    identifier = StringEscapeUtils.escapeXml11(identifier);

    metadata.setTitle(identifier);
  }
}
