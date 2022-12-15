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

package com.spricoder.ddbs.mongo;

import com.spricoder.ddbs.data.Article;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ArticleRepository extends MongoRepository<Article, String> {
  @Cacheable(cacheNames = "ArticleByAid", key = "#a0")
  @Query("{aid:'?0'}")
  Article findByAid(String aid);

  @Cacheable(cacheNames = "ArticleByTag", key = "#a0")
  @Query("{articleTags:'?0'}")
  List<Article> findByTag(String tag);

  @Cacheable(cacheNames = "ArticleByCategory", key = "#a0 +' '+ #a1")
  @Query("{category:'?0', articleTags:'?1'}")
  List<Article> findByCategoryAndTag(String category, String tag);
}
