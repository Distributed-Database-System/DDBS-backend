package com.spricoder.ddbs.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetailVO {

  private String aid;
  private String timestamp;
  private String title;
  private String category;
  private String articleAbstract;
  private String tags;
  private String authors;
  private String language;
  private String text;
  private List<String> imageList;
  private String video;
  private Integer readNum;
  private Integer agreeNum;
  private Integer commentNum;
  private Integer shareNum;
}
