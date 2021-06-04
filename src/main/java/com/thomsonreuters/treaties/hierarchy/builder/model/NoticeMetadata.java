package com.thomsonreuters.treaties.hierarchy.builder.model;

import lombok.Data;

import java.nio.file.Path;

@Data
public class NoticeMetadata {
  private String celex;
  private String title;
  private String eli;
  private Path filePath;
}
