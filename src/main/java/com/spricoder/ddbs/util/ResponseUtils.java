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

package com.spricoder.ddbs.util;

import com.spricoder.ddbs.vo.ResponseVO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/** @author cyz 生成返回对象工具类 */
public class ResponseUtils {
  private static ObjectMapper objectMapper;
  private static HttpHeaders headersJson;

  static {
    objectMapper = new ObjectMapper();
    headersJson = new HttpHeaders();
    headersJson.setContentType(MediaType.APPLICATION_JSON);
  }

  public static <T> ResponseEntity<T> create(T data, HttpStatus status) {
    return new ResponseEntity<>(data, headersJson, status);
  }

  public static <T> ResponseEntity<T> success(T data) {
    return create(data, HttpStatus.OK);
  }

  public static <T> ResponseEntity<ResponseVO> success() {
    return create(ResponseVO.buildSuccess(), HttpStatus.OK);
  }

  public static <T> ResponseEntity<ResponseVO> success(String message) {
    return create(ResponseVO.buildSuccess(message), HttpStatus.OK);
  }

  public static ResponseEntity<ResponseVO> failure(String message) {
    return create(ResponseVO.buildFailure(message), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public static ResponseEntity<ResponseVO> failure(ResponseVO data) {
    return create(data, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
