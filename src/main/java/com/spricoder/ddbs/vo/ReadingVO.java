package com.spricoder.ddbs.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadingVO {

  private String aid;
  private String title;
  private String timestamp;
  private String region;
  private String category;
  private Integer readTimeLength;
  private String agreeOrNot;
  private String commentOrNot;
  private String commentDetail;
  private String shareOrNot;
}