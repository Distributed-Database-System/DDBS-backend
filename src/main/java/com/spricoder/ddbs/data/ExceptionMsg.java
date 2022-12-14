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

package com.spricoder.ddbs.data;

import java.time.LocalDateTime;

public class ExceptionMsg {
  private Integer exceptionId;
  private Integer code;
  private String msg;
  private LocalDateTime happenTime;

  public Integer getExceptionId() {
    return exceptionId;
  }

  public void setExceptionId(Integer exceptionId) {
    this.exceptionId = exceptionId;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public LocalDateTime getHappenTime() {
    return happenTime;
  }

  public void setHappenTime(LocalDateTime happenTime) {
    this.happenTime = happenTime;
  }

  @Override
  public String toString() {
    return "ExceptionMsg{"
        + "exceptionId="
        + exceptionId
        + ", code="
        + code
        + ", msg='"
        + msg
        + '\''
        + ", happenTime="
        + happenTime
        + '}';
  }
}
