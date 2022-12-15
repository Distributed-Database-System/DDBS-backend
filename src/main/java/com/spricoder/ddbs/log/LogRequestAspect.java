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

package com.spricoder.ddbs.log;

import com.spricoder.ddbs.bl.MonitorService;
import com.spricoder.ddbs.data.Log;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class LogRequestAspect {
  @Autowired MonitorService monitorService;

  @Pointcut("execution(public * com.spricoder.ddbs.controller..*.*(..))")
  public void log() {}

  /**
   * 环绕切面
   *
   * @param proceedingJoinPoint
   * @return
   * @throws Throwable
   */
  @Around("log()")
  public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    Log myLog = new Log();
    long startTime = System.currentTimeMillis();
    ResponseEntity<?> result;
    // 执行方法前
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      HttpServletRequest request = attributes.getRequest();
      if (request.getRequestURI() != null && request.getRequestURI().length() != 0) {
        myLog.setIp(request.getRemoteAddr());
        myLog.setMethod(request.getMethod());
        StringBuilder uri = new StringBuilder(request.getRequestURI());
        if (request.getQueryString() != null) {
          uri.append("?");
          uri.append(request.getQueryString());
        }
        String args = new Gson().toJson(proceedingJoinPoint.getArgs());
        myLog.setArgs(args);
        myLog.setUrl(uri.toString());
        myLog.setRequestTime(LocalDateTime.now());

        log.debug("Before:" + myLog);
      }
      // 执行方法
      result = (ResponseEntity<?>) proceedingJoinPoint.proceed();

      if (request.getRequestURI() != null && request.getRequestURI().length() != 0) {
        // 执行方法后
        myLog.setCode(result != null ? result.getStatusCode().value() : HttpStatus.OK.value());
        myLog.setProcessTime(System.currentTimeMillis() - startTime);

        monitorService.addLog(myLog);
        log.debug("After:" + myLog);
      }
    } else {
      result = (ResponseEntity<?>) proceedingJoinPoint.proceed();
      log.error("ServletRequestAttributes is empty.");
    }
    return result;
  }
}
