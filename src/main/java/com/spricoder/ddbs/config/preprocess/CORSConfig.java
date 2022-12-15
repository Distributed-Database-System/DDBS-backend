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

package com.spricoder.ddbs.config.preprocess;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/** @Author spricoder 全局跨域处理 Create by 2021/03/07 @Version 1.0 */
@Configuration
public class CORSConfig {
  private static String[] originsVal =
      new String[] {"localhost:8002", "localhost:8080", "127.0.0.1:8002", "127.0.0.1"};

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    addAllowedOrigins(corsConfiguration);
    // 允许token放置于请求头
    corsConfiguration.addExposedHeader("token");
    // 2
    corsConfiguration.addAllowedHeader("*");
    // 3
    corsConfiguration.addAllowedMethod("*");
    // 跨域session共享
    corsConfiguration.setAllowCredentials(true);
    // 4
    source.registerCorsConfiguration("/**", corsConfiguration);
    return new CorsFilter(source);
  }

  private void addAllowedOrigins(CorsConfiguration corsConfiguration) {
    for (String origin : originsVal) {
      corsConfiguration.addAllowedOrigin("http://" + origin);
      corsConfiguration.addAllowedOrigin("https://" + origin);
    }
  }
}
