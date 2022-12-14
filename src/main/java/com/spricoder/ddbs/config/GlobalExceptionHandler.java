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

package com.spricoder.ddbs.config;

import com.spricoder.ddbs.bl.MonitorService;
import com.spricoder.ddbs.constant.MyResponse;
import com.spricoder.ddbs.constant.ResponseCode;
import com.spricoder.ddbs.constant.ServerException;
import com.spricoder.ddbs.data.ExceptionMsg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice(basePackages = {"com.spricoder.ddbs"})
@Component
public class GlobalExceptionHandler {
  @Autowired MonitorService monitorService;

  private static int exceptionNumber = 1;

  @ResponseBody
  @ExceptionHandler(value = Exception.class)
  public MyResponse errorHandler(Exception e) {
    synchronized (this) {
      if (e instanceof MethodArgumentNotValidException) {
        // check post exception
        Map<String, String> errors = new HashMap<>();
        MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
        ex.getBindingResult()
            .getAllErrors()
            .forEach(
                (error) -> {
                  String fieldName = ((FieldError) error).getField();
                  String errorMessage = error.getDefaultMessage();
                  errors.put(fieldName, errorMessage);
                });
        return MyResponse.error(errors);
      }
      if (e instanceof ConstraintViolationException) {
        // check get exception
        ConstraintViolationException ex = (ConstraintViolationException) e;
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations()
            .forEach(
                (error) -> {
                  String fieldName = error.getPropertyPath().toString();
                  String errorMessage = error.getMessage();
                  errors.put(fieldName, errorMessage);
                });
        return MyResponse.error(errors);
      } else if (e instanceof ServerException) {
        // check known exception
        ServerException serverException = (ServerException) e;
        e.printStackTrace();
        return new MyResponse(serverException.getCode(), e.getMessage());
      } else {
        // check normal exception
        StringBuilder exceptionStatus = new StringBuilder("Exception");
        if (e instanceof HttpMessageNotReadableException) {
          exceptionStatus.append(": format error");
        } else {
          exceptionStatus.append(e.getMessage());
        }

        ExceptionMsg exceptionMsg = new ExceptionMsg();
        exceptionMsg.setExceptionId(exceptionNumber);
        exceptionMsg.setCode(ResponseCode.CATCH_EXCEPTION);
        exceptionMsg.setMsg(getStackTraceInfo(e));
        exceptionMsg.setHappenTime(LocalDateTime.now());
        exceptionNumber++;
        try {
          monitorService.addException(exceptionMsg);
          log.error(exceptionStatus + exceptionMsg.getMsg());
        } catch (Exception exception) {
          log.error(exceptionStatus + exceptionMsg.getMsg());
        }
        return MyResponse.exception(exceptionStatus.toString());
      }
    }
  }

  public static String getStackTraceInfo(Exception e) {
    StringWriter sw = null;
    PrintWriter pw = null;
    try {
      sw = new StringWriter();
      pw = new PrintWriter(sw);
      // ??????????????????????????????printWriter???
      e.printStackTrace(pw);
      pw.flush();
      sw.flush();
      return sw.toString();
    } catch (Exception ex) {
      log.error("Failed to transform", ex);
      return "Failed to transform";
    } finally {
      if (sw != null) {
        try {
          sw.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
      if (pw != null) {
        pw.close();
      }
    }
  }
}
