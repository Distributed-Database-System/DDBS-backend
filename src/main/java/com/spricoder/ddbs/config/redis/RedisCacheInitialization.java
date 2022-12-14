package com.spricoder.ddbs.config.redis;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RedisCacheInitialization implements CommandLineRunner {

  private final RedisTemplate<String, Object> redisTemplate;

  public RedisCacheInitialization(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public void run(String... args) throws Exception {
    try {
      Set<String> keys = redisTemplate.keys("*");
      if (keys != null) {
        redisTemplate.delete(keys);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
