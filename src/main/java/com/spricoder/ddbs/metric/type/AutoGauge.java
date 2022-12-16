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

package com.spricoder.ddbs.metric.type;

import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;

import java.lang.ref.WeakReference;
import java.util.function.ToLongFunction;

@Slf4j
public class AutoGauge<T> implements IMetric {
  private final WeakReference<T> refObject;
  private final ToLongFunction<T> mapper;

  public AutoGauge(
      io.micrometer.core.instrument.MeterRegistry meterRegistry,
      String metricName,
      T object,
      ToLongFunction<T> mapper,
      String... tags) {
    log.info("{},{}", metricName, tags);
    this.refObject =
        new WeakReference<>(
            meterRegistry.gauge(
                metricName, Tags.of(tags), object, value -> (double) mapper.applyAsLong(value)));
    this.mapper = mapper;
  }

  public long value() {
    if (refObject.get() == null) {
      return 0L;
    }
    return mapper.applyAsLong(refObject.get());
  }
}
