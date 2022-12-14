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
