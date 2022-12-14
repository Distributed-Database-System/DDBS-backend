package com.spricoder.ddbs.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ArticleUpsertVO {

  private String aid;
  private String timestamp;
  private String title;
  private String category;
  private String articleAbstract;
  private String tags;
  private String authors;
  private String language;
  private String text;
}
