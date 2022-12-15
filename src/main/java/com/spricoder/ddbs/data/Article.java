/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
@Document("article")
public class Article implements Serializable {
  @Id private String mongoId;

  @Field("id")
  private String id;

  @Field("aid")
  private String aid;

  @Field("timestamp")
  private String timestamp;

  @Field("title")
  private String title;

  @Field("category")
  private String category;

  @Field("abstract")
  private String articleAbstract;

  @Field("articleTags")
  private String articleTags;

  @Field("authors")
  private String authors;

  @Field("language")
  private String language;

  @Field("text")
  private String text;

  @Field("image")
  private String image;

  @Field("video")
  private String video;
}
