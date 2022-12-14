package com.spricoder.ddbs.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("user")
public class User implements Serializable {
  @Id private String mongoId;

  @Field("id")
  private String id;

  @Field("uid")
  private String uid;

  @Field("timestamp")
  private String timestamp;

  @Field("name")
  private String name;

  @Field("gender")
  private String gender;

  @Field("email")
  private String email;

  @Field("phone")
  private String phone;

  @Field("dept")
  private String dept;

  @Field("grade")
  private String grade;

  @Field("language")
  private String language;

  @Field("region")
  private String region;

  @Field("role")
  private String role;

  @Field("preferTags")
  private String preferTags;

  @Field("obtainedCredits")
  private Integer obtainedCredits;
}
