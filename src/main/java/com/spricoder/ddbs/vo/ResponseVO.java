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

package com.spricoder.ddbs.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.ObjectError;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVO<T> {
  private boolean success;

  private String message;

  public ResponseVO(Boolean success, String message) {
    this.success = success;
    this.message = message;
  }

  public static ResponseVO buildSuccess(String msg) {
    return new ResponseVO(true, msg);
  }

  public static ResponseVO buildSuccess() {
    return new ResponseVO(true, "");
  }

  public static ResponseVO buildFailure(String msg) {
    return new ResponseVO(false, msg);
  }

  public static ResponseVO buildError(List<ObjectError> constraintViolations) {
    Set<String> setMessage = new HashSet<>();
    for (ObjectError message : constraintViolations) {
      setMessage.add(message.getDefaultMessage());
    }
    return buildFailure(setMessage.toString());
  }
}
