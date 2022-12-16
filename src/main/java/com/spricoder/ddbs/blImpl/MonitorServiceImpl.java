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

package com.spricoder.ddbs.blImpl;

import com.spricoder.ddbs.bl.MonitorService;
import com.spricoder.ddbs.data.ExceptionMsg;
import com.spricoder.ddbs.data.Log;
import com.spricoder.ddbs.metric.MetricService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MonitorServiceImpl implements MonitorService {
  @Autowired MetricService metricService;

  @Override
  public void addException(ExceptionMsg exceptionMsg) {
    metricService
        .getOrCreateCounter(
            "code", "type", "exception", "value", String.valueOf(exceptionMsg.getCode()))
        .inc();
  }

  @Override
  public void addLog(Log log) {
    metricService.getOrCreateCounter("request", "type", "ip", "value", log.getIp()).inc();
    metricService.getOrCreateCounter("request", "type", "method", "value", log.getMethod()).inc();
    metricService.getOrCreateCounter("request", "type", "url", "value", log.getUrl()).inc();
    metricService
        .getOrCreateCounter("request", "type", "code", "value", String.valueOf(log.getCode()))
        .inc();
    metricService
        .getOrCreateHistogram("consumed", "type", "ip", "value", log.getIp())
        .update(log.getProcessTime());
    metricService
        .getOrCreateHistogram("consumed", "type", "method", "value", log.getMethod())
        .update(log.getProcessTime());
    metricService
        .getOrCreateHistogram("consumed", "type", "url", "value", log.getUrl())
        .update(log.getProcessTime());
    metricService
        .getOrCreateHistogram("consumed", "type", "value", String.valueOf(log.getCode()))
        .update(log.getProcessTime());
  }

  @Override
  public String scrape() {
    return metricService.scrape();
  }
}
