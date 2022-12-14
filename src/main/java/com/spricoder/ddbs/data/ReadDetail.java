package com.spricoder.ddbs.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Document("read")
public class ReadDetail implements Serializable {
  @Id private String mongoId;

  @Field("id")
  private String id;

  @Field("uid")
  private String uid;

  @Field("aid")
  private String aid;

  @Field("timestamp")
  private String timestamp;

  @Field("region")
  private String region;

  @Field("category")
  private String category;

  @Field("readTimeLength")
  private Integer readTimeLength;

  @Field("agreeOrNot")
  private String agreeOrNot;

  @Field("commentOrNot")
  private String commentOrNot;

  @Field("commentDetail")
  private String commentDetail;

  @Field("shareOrNot")
  private String shareOrNot;
}
