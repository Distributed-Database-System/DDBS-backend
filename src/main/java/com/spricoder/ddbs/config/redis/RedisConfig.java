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

/** @description redis配置 配置序列化方式以及缓存管理器 */
@EnableCaching // 开启缓存
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfig extends CachingConfigurerSupport {

  /**
   * 配置自定义redisTemplate
   *
   * @param connectionFactory
   * @return
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setValueSerializer(jackson2JsonRedisSerializer());
    // 使用StringRedisSerializer来序列化和反序列化redis的key值
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
   * json序列化
   *
   * @return
   */
  @Bean
  public RedisSerializer<Object> jackson2JsonRedisSerializer() {
    // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
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
   * 缓存管理器配置
   *
   * @param redisConnectionFactory
   * @return
   */
  @Bean
  public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
    // 设置缓存的默认过期时间，也是使用Duration设置
    config =
        config
            .entryTtl(Duration.ofMinutes(1))
            // 设置 key为string序列化
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()))
            // 设置value为json序列化
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    jackson2JsonRedisSerializer()))
            // 不缓存空值
            .disableCachingNullValues();

    // 设置一个初始化的缓存空间set集合
    Set<String> cacheNames = new HashSet<>();
    cacheNames.add("timeGroup");
    cacheNames.add("user");

    // 对每个缓存空间应用不同的配置
    Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
    configMap.put("timeGroup", config);
    configMap.put("user", config.entryTtl(Duration.ofSeconds(120)));

    // 使用自定义的缓存配置初始化一个cacheManager
    RedisCacheManager cacheManager =
        RedisCacheManager.builder(redisConnectionFactory)
            // 一定要先调用该方法设置初始化的缓存名，再初始化相关的配置
            .initialCacheNames(cacheNames)
            .withInitialCacheConfigurations(configMap)
            .build();
    return cacheManager;
  }

  /** 缓存的key是 包名+方法名+参数列表 */
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
