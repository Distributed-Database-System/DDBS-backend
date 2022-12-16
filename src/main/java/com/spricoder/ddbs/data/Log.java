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

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Log {
  private String ip;
  private String method;
  private String url;
  private String args;
  private Integer code;
  private Long processTime;
  private LocalDateTime requestTime;

  @Override
  public String toString() {
    return "Log{"
        + " ip='"
        + ip
        + '\''
        + ", method='"
        + method
        + '\''
        + ", url='"
        + url
        + '\''
        + ", args='"
        + args
        + '\''
        + ", code="
        + code
        + ", processTime="
        + processTime
        + ", requestTime="
        + requestTime
        + '}';
  }
}
