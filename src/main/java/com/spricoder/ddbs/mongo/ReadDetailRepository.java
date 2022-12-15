package com.spricoder.ddbs.mongo;

import com.spricoder.ddbs.data.ReadDetail;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ReadDetailRepository extends MongoRepository<ReadDetail, String> {
  @Cacheable(cacheNames = "ReadDetailByUid", key = "#a0")
  @Query("{uid:'?0'}")
  List<ReadDetail> findByUid(String uid);

  @Cacheable(cacheNames = "ReadDetailByAid", key = "#a0")
  @Query("{aid:'?0'}")
  List<ReadDetail> findByAid(String aid);

  @Cacheable(cacheNames = "ReadDetailByUidAndAid", key = "#a0 +' '+ #a1")
  @Query("{uid:'?0', aid:'?1'}")
  List<ReadDetail> findByUidAndAid(String uid, String aid);
}
