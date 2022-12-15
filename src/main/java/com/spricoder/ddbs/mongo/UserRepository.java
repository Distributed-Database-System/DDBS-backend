package com.spricoder.ddbs.mongo;

import com.spricoder.ddbs.data.User;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
  @Cacheable(cacheNames = "UserByUid", key = "#a0")
  @Query("{uid:'?0'}")
  User findByUid(String uid);

  @Cacheable(cacheNames = "UserByPreferTagOrderByCredits", key = "#a0")
  @Query(value = "{preferTags:'?0'}", sort = "{obtainedCredits: -1}")
  List<User> findByPreferTagOrderByCredits(String tag);
}
