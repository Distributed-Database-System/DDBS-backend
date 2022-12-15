package com.spricoder.ddbs.bl;

public interface RedisService {
  void set(String key, Object value);

  String get(String key);

  void setTime(String key, Object value, long seconds);

  boolean delete(String key);
}
