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

import java.util.function.Supplier;

public class MyResponse {
  private int code;
  private Object data;
  private transient String exception;

  public MyResponse() {
    this.code = ResponseCode.OK;
    this.data = "Success";
  }

  public MyResponse(int code) {
    this.code = code;
  }

  public MyResponse(boolean success) {
    this.code = success ? 0 : -1;
    this.data = null;
  }

  public MyResponse(int code, Object data) {
    this.code = code;
    this.data = data;
  }

  public MyResponse(int code, Object data, String exception) {
    this.code = code;
    this.data = data;
    this.exception = exception;
  }

  /**
   * 用来处理一般的异常
   *
   * @return
   */
  public static MyResponse exception(Object data) {
    return new MyResponse(ResponseCode.CATCH_EXCEPTION, data);
  }

  public static MyResponse error(Object data) {
    return new MyResponse(ResponseCode.Error, data);
  }

  public static MyResponse ok(Object data) {
    return new MyResponse(ResponseCode.OK, data);
  }

  /** @return if data==null return -3 */
  public static MyResponse checkNull(Object data) {
    if (data == null) {
      return new MyResponse(ResponseCode.RESULT_IS_NULL, null);
    } else {
      return ok(data);
    }
  }

  public static MyResponse checkBoolean(boolean result) {
    return new MyResponse(result);
  }

  public static MyResponse ifTrue(boolean result, Supplier<?> data) {
    if (result) {
      return ok(data.get());
    }
    return new MyResponse(false);
  }

  public static MyResponse checkForbidden(boolean result) {
    if (result == true) {
      return MyResponse.ok(null);
    } else {
      return new MyResponse(403);
    }
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }
}
