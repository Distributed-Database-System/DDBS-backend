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
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class HDFSManager {

  private static final String hdfsDirPath = "/hdfs-test/";
  private static final String hdfsFilePath = hdfsDirPath + "wordCountTest.txt";
  private static final String hdfsFileContent = "HELLO HDFS";

  private final FileSystem fs;

  public HDFSManager(FileSystem fs) {
    this.fs = fs;
  }

  public String createFile() throws IOException {
    Path path = new Path(hdfsFilePath);
    System.out.println(path);
    FSDataOutputStream outputStream = fs.create(path);
    byte[] buff = hdfsFileContent.getBytes();
    outputStream.write(buff, 0, buff.length);
    outputStream.hflush();
    System.out.println(fs.getFileStatus(path));
    return fs.getFileStatus(path).toString();
  }

  public InputStream getFileInputStream(String hdfsFilePath) throws IOException {
    return fs.open(new Path(hdfsFilePath));
  }
}
