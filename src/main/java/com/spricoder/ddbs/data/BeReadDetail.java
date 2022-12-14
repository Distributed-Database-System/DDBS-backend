package com.spricoder.ddbs.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@Document("beread")
public class BeReadDetail implements Serializable {
  @Id private String mongoId;

  @Field("timestamp")
  private String timestamp;

  @Field("aid")
  private String aid;

  @Field("category")
  private String category;

  @Field("readNum")
  private Integer readNum;

  @Field("readUidList")
  private List<String> readUidList;

  @Field("agreeNum")
  private Integer agreeNum;

  @Field("agreeUidList")
  private List<String> agreeUidList;

  @Field("commentNum")
  private Integer commentNum;

  @Field("commentUidList")
  private List<String> commentUidList;

  @Field("shareNum")
  private Integer shareNum;

  @Field("shareUidList")
  private List<String> shareUidList;
}
