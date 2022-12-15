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

package com.spricoder.ddbs.config.preprocess;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.Charset;

@Slf4j
public class RequestWrapper extends HttpServletRequestWrapper {
  private final byte[] body;

  public RequestWrapper(HttpServletRequest request) throws IOException {
    super(request);
    String bodyStr = getBodyString(request);
    body = bodyStr.getBytes(Charset.defaultCharset());
  }

  public String getBodyString(final ServletRequest request) {
    try {
      return inputStream2String(request.getInputStream());
    } catch (IOException e) {
      log.error("", e);
      throw new RuntimeException(e);
    }
  }

  public String getBodyString() {
    final InputStream inputStream = new ByteArrayInputStream(body);

    return inputStream2String(inputStream);
  }

  private String inputStream2String(InputStream inputStream) {
    StringBuilder sb = new StringBuilder();
    BufferedReader reader = null;

    try {
      reader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      log.error("", e);
      throw new RuntimeException(e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          log.error("", e);
        }
      }
    }

    return sb.toString();
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {

    final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);

    return new ServletInputStream() {
      @Override
      public int read() throws IOException {
        return inputStream.read();
      }

      @Override
      public boolean isFinished() {
        return false;
      }

      @Override
      public boolean isReady() {
        return false;
      }

      @Override
      public void setReadListener(ReadListener readListener) {}
    };
  }
}
