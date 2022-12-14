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

package com.spricoder.ddbs.constant;

public class ServerException extends RuntimeException {
  private int code;
  private String msg;

  public ServerException() {}

  public ServerException(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public ServerException(String message, int code, String msg) {
    super(message);
    this.code = code;
    this.msg = msg;
  }

  public ServerException(String message, Throwable cause, int code, String msg) {
    super(message, cause);
    this.code = code;
    this.msg = msg;
  }

  public ServerException(Throwable cause, int code, String msg) {
    super(cause);
    this.code = code;
    this.msg = msg;
  }

  public ServerException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      int code,
      String msg) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.code = code;
    this.msg = msg;
  }

  public String getMessage() {
    return this.msg;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
