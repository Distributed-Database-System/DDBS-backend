package com.spricoder.ddbs.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleVO {

  private String aid;
  private String title;
  private String category;
  private String articleAbstract;
  private String authors;
}
