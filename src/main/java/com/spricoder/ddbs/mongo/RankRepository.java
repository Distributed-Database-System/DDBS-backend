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

import com.spricoder.ddbs.data.Rank;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface RankRepository extends MongoRepository<Rank, String> {
  /**
   * Get rank of the specific time
   *
   * @param time the format is year:%Y-month:%m, year:%Y-week:%U, year:%Y-day:%j
   * @return articles sorted in descending order
   */
  @Cacheable(cacheNames = "RankByTime", key = "#a0")
  @Query("{timestamp:'?0'}")
  Rank findByTime(String time);

  /**
   * Get ranks of the specific temporal granularity
   *
   * @param temporalGranularity three granularities, including monthly, weekly, daily
   * @return ranks of specific
   */
  @Cacheable(cacheNames = "RankByTemporalGranularity", key = "#a0")
  @Query("{temporal_granularity:'?0'}")
  List<Rank> findByTemporalGranularity(String temporalGranularity);
}
