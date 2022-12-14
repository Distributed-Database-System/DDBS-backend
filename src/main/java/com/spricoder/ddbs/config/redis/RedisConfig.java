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

package com.spricoder.ddbs.config.redis;

import com.spricoder.ddbs.data.Rank;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** @description redis?????? ?????????????????????????????????????????? */
@EnableCaching // ????????????
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfig extends CachingConfigurerSupport {

  /**
   * ???????????????redisTemplate
   *
   * @param connectionFactory
   * @return
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setValueSerializer(jackson2JsonRedisSerializer());
    // ??????StringRedisSerializer???????????????????????????redis???key???
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(jackson2JsonRedisSerializer());
    template.afterPropertiesSet();
    return template;
  }

  public static void main(String[] args) {
    Jackson2JsonRedisSerializer<Object> serializer =
        new Jackson2JsonRedisSerializer<Object>(Object.class);
    List<Rank> rankList = new ArrayList<>();
    rankList.add(new Rank("1", "1", "1", Collections.emptyList(), Collections.emptyList()));
    rankList.add(new Rank("1", "1", "1", Collections.emptyList(), Collections.emptyList()));
    rankList.add(new Rank("1", "1", "1", Collections.emptyList(), Collections.emptyList()));
    byte[] bytes = serializer.serialize(rankList);
    System.out.println(new String(bytes));
  }

  /**
   * json?????????
   *
   * @return
   */
  @Bean
  public RedisSerializer<Object> jackson2JsonRedisSerializer() {
    // ??????Jackson2JsonRedisSerializer???????????????????????????redis???value???
    Jackson2JsonRedisSerializer<Object> serializer =
        new Jackson2JsonRedisSerializer<Object>(Object.class);

    ObjectMapper mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    mapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
    serializer.setObjectMapper(mapper);
    return serializer;
  }

  /**
   * ?????????????????????
   *
   * @param redisConnectionFactory
   * @return
   */
  @Bean
  public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    // ?????????????????????????????????config??????????????????????????????????????????
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
    // ????????????????????????????????????????????????Duration??????
    config =
        config
            .entryTtl(Duration.ofMinutes(1))
            // ?????? key???string?????????
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()))
            // ??????value???json?????????
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    jackson2JsonRedisSerializer()))
            // ???????????????
            .disableCachingNullValues();

    // ????????????????????????????????????set??????
    Set<String> cacheNames = new HashSet<>();
    cacheNames.add("timeGroup");
    cacheNames.add("user");

    // ??????????????????????????????????????????
    Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
    configMap.put("timeGroup", config);
    configMap.put("user", config.entryTtl(Duration.ofSeconds(120)));

    // ?????????????????????????????????????????????cacheManager
    RedisCacheManager cacheManager =
        RedisCacheManager.builder(redisConnectionFactory)
            // ????????????????????????????????????????????????????????????????????????????????????
            .initialCacheNames(cacheNames)
            .withInitialCacheConfigurations(configMap)
            .build();
    return cacheManager;
  }

  /** ?????????key??? ??????+?????????+???????????? */
  @Bean
  public KeyGenerator keyGenerator() {
    return (target, method, params) -> {
      StringBuilder sb = new StringBuilder();
      for (Object obj : params) {
        sb.append(obj.toString());
      }
      return sb.toString();
    };
  }
}
