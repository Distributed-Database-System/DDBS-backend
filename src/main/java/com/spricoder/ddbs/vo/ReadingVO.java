package com.spricoder.ddbs.vo;

import com.spricoder.ddbs.data.ReadDetail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

  public ReadingVO(ReadDetail readDetail) {
    this.aid = readDetail.getAid();
    // title
    this.timestamp = readDetail.getTimestamp();
    this.region = readDetail.getRegion();
    this.category = readDetail.getCategory();
    this.readTimeLength = readDetail.getReadTimeLength();
    this.agreeOrNot = readDetail.getAgreeOrNot();
    this.commentOrNot = readDetail.getCommentOrNot();
    this.shareOrNot = readDetail.getShareOrNot();
    this.commentDetail = readDetail.getCommentDetail();
  }
}
