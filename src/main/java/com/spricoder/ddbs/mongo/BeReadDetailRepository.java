package com.spricoder.ddbs.mongo;

import com.spricoder.ddbs.data.BeReadDetail;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface BeReadDetailRepository extends MongoRepository<BeReadDetail, String> {
  @Cacheable(cacheNames = "BeReadFindByAid", key = "#a0")
  @Query("{aid:'?0'}")
  BeReadDetail findByAid(String aid);
}
