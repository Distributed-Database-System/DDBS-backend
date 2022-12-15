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
