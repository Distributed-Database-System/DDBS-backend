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

package com.spricoder.ddbs.config.hdfs;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Configuration
public class HDFSConfig {
  @Value("${spring.data.hdfs.uri}")
  private String hdfsPath;

  @Bean
  public org.apache.hadoop.conf.Configuration getConfiguration() {
    org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
    configuration.set("fs.defaultFS", hdfsPath);
    return configuration;
  }

  @Bean
  public FileSystem getFileSystem() {
    FileSystem fileSystem = null;
    try {
      String hdfsName = "root";
      fileSystem = FileSystem.get(new URI(hdfsPath), getConfiguration(), hdfsName);
    } catch (IOException | URISyntaxException | InterruptedException e) {
      log.error(e.getMessage());
      e.printStackTrace();
    }
    return fileSystem;
  }
}
