package com.spricoder.ddbs.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticleVO {

  private String aid;
  private String title;
  private String category;
  private String articleAbstract;
  private String authors;
}
