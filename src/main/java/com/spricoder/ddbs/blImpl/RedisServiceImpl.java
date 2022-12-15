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

package com.spricoder.ddbs.blImpl;

import com.spricoder.ddbs.bl.RedisService;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.time.Duration;
import java.time.LocalDateTime;

/** @Author spricoder Create by 2020/07/05 @Version 1.0 */
@Service
public class RedisServiceImpl implements RedisService {
  @Resource private RedisTemplate<String, Object> redisTemplate;

  private static volatile ValueOperations<String, Object> vo;

  /** 保证单件 */
  private void initRedis() {
    if (vo == null) {
      synchronized (RedisServiceImpl.class) {
        if (vo == null) {
          vo = redisTemplate.opsForValue();
        }
      }
    }
  }

  /**
   * 设置键值
   *
   * @param key
   * @param value
   */
  @Override
  public void set(String key, Object value) {
    initRedis();
    vo.set(key, value);
  }

  /**
   * 获取到键的值
   *
   * @param key
   * @return
   */
  @Override
  public String get(String key) {
    initRedis();
    return (String) vo.get(key);
  }

  /**
   * 放置有时效性的键
   *
   * @param key
   * @param value
   * @param seconds 秒
   */
  @Override
  public void setTime(String key, Object value, long seconds) {
    initRedis();
    Duration duration =
        Duration.between(LocalDateTime.now(), LocalDateTime.now().plusSeconds(seconds));
    vo.set(key, value, duration);
  }

  /**
   * 删除对应的键
   *
   * @param key
   * @return
   */
  @Override
  public boolean delete(String key) {
    initRedis();
    try {
      redisTemplate.delete(key);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }
}
