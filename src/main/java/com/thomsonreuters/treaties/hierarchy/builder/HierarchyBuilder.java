package com.thomsonreuters.treaties.hierarchy.builder;

import com.google.common.collect.Iterables;
import com.thomsonreuters.treaties.hierarchy.builder.extractor.NoticeMetadataProvider;
import com.thomsonreuters.treaties.hierarchy.builder.model.Element;
import com.thomsonreuters.treaties.hierarchy.builder.model.Hierarchy;
import com.thomsonreuters.treaties.hierarchy.builder.model.NoticeMetadata;
import com.thomsonreuters.treaties.hierarchy.builder.model.PathItem;
import com.thomsonreuters.treaties.hierarchy.builder.rule.engine.RuleEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HierarchyBuilder {
  @Autowired
  private RuleEngine ruleEngine;

  @Autowired
  private NoticeMetadataProvider metadataProvider;

  public Hierarchy build(Path folder) {
    // reading all the notices from the folder
    final List<Path> notices = Arrays.asList(folder.toFile().listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return StringUtils.endsWith(name, "_notice.xml");
      }
    }))
        .stream()
        .map(File::toPath)
        .collect(Collectors.toList());

    log.info("Reading folder " + folder.getFileName().toString());

    final Hierarchy hierarchy = new Hierarchy(getRootCelex(folder));
    for (Path notice : notices) {
      log.info("... Reading notice " + notice.getFileName().toString());

      final NoticeMetadata noticeMetadata = metadataProvider.provide(notice);
      final Collection<PathItem> path = ruleEngine.process(noticeMetadata);

      Element lastAdded = hierarchy.getRoot();

      if (path.isEmpty()) {
        hierarchy.addUnknown(
            lastAdded,
            noticeMetadata.getCelex(),
            noticeMetadata.getTitle()
        );
        log.warn("Can't detect path for notice with celex {}", noticeMetadata.getCelex());
        continue;
      }

      for (PathItem item : path) {
        String celex = "";
        String title = "";
        if (isLast(item, path)) {
          celex = noticeMetadata.getCelex();
          title = noticeMetadata.getTitle();
        }

        if (StringUtils.equalsIgnoreCase(item.getElement(), "art")) {
          // this is article
          lastAdded = hierarchy.addArticle(
              lastAdded,
              item.getNumber(),
              celex,
              title
          );
        } else if (StringUtils.equalsIgnoreCase(item.getElement(), "pbl")) {
          // this is preamble
          lastAdded = hierarchy.addPreamble(
              lastAdded,
              item.getNumber(),
              celex,
              title
          );
        } else if (StringUtils.equalsIgnoreCase(item.getElement(), "anx")) {
          // this is annex
          lastAdded = hierarchy.addAnnex(
              lastAdded,
              item.getNumber(),
              celex,
              title
          );
        } else if (StringUtils.equalsIgnoreCase(item.getElement(), "cnv")) {
          // this look like an unknown structural element, I've noticed
          // it allows splitting the treaty into huge chunks like
          // the very beginning, the main part, other stuff
          lastAdded = hierarchy.addElement(
              lastAdded,
              "eu-fake-cnv",
              item.getNumber(),
              celex,
              title
          );
        } else if (StringUtils.equalsIgnoreCase(item.getElement(), "par")) {
          // piece of the bigger content, from the content I see it's article
          lastAdded = hierarchy.addArticle(
              lastAdded,
              item.getNumber(),
              celex,
              title
          );
        } else if (StringUtils.equalsIgnoreCase(item.getElement(), "exl")) {
          // again, as well as cnv this looks like a structural element
          lastAdded = hierarchy.addElement(
              lastAdded,
              "eu-fake-exl",
              item.getNumber(),
              celex,
              title
          );
        } else if (
            StringUtils.equalsIgnoreCase(item.getElement(), "pro") ||
            StringUtils.equalsIgnoreCase(item.getElement(), "vpro")
        ) {
          // this is a protocol, they may be in the separate hierarchy or in the annex
          lastAdded = hierarchy.addProtocol(
              lastAdded,
              item.getNumber(),
              celex,
              title
          );
        } else if (StringUtils.equalsIgnoreCase(item.getElement(), "fna")) {
          // final
          lastAdded = hierarchy.addFinal(
              lastAdded,
              item.getNumber(),
              celex,
              title
          );
        } else if (StringUtils.equalsIgnoreCase(item.getElement(), "act")) {
          // act, but they're not supported. Let's create them as a separate data structure
          // fake element
          lastAdded = hierarchy.addAct(
              lastAdded,
              item.getNumber(),
              celex,
              title
          );
        } else if (StringUtils.equalsIgnoreCase(item.getElement(), "dcl")) {
          // declaration
          // fake element
          lastAdded = hierarchy.addDeclaration(
              lastAdded,
              item.getNumber(),
              celex,
              title
          );
        } else if (StringUtils.equalsIgnoreCase(item.getElement(), "pcd")) {
          // procedure
          // fake element
          lastAdded = hierarchy.addElement(
              lastAdded,
              "eu-fake-procedure",
              item.getNumber(),
              celex,
              title
          );
        } else if (StringUtils.equalsIgnoreCase(item.getElement(), "agr")) {
          // agreement
          // fake element
          lastAdded = hierarchy.addElement(
              lastAdded,
              "eu-fake-agreement",
              item.getNumber(),
              celex,
              title
          );
        } else if (StringUtils.equalsIgnoreCase(item.getElement(), "mnt")) {
          // minutes of signing
          // fake element
          lastAdded = hierarchy.addElement(
              lastAdded,
              "eu-fake-minutes",
              item.getNumber(),
              celex,
              title
          );
        } else if (StringUtils.equalsIgnoreCase(item.getElement(), "corrigendum")) {
          // corrigendum, I'm surprised
          // fake element
          lastAdded = hierarchy.addElement(
              lastAdded,
              "eu-fake-corrigendum",
              item.getNumber(),
              celex,
              title
          );
        } else if (StringUtils.equalsIgnoreCase(item.getElement(), "unknown-element")) {
          // completely unknown element
          lastAdded = hierarchy.addElement(
              lastAdded,
              "eu-fake-unknown",
              item.getNumber(),
              celex,
              title
          );
        } else if (StringUtils.equalsIgnoreCase(item.getElement(), "tab")) {
          // Tables of equivalences
          // fake element
          lastAdded = hierarchy.addElement(
              lastAdded,
              "eu-fake-tab",
              item.getNumber(),
              celex,
              title
          );
        } else {
          log.warn("Unknown element type {}", item.getElement());
          System.exit(1);
        }
      }
    }

    return hierarchy;
  }

  private boolean isLast(PathItem item, Collection<PathItem> path) {
    return Iterables.getLast(path).equals(item);
  }

  private String getRootCelex(Path path) {
    return path.getFileName().toString();
  }
}
