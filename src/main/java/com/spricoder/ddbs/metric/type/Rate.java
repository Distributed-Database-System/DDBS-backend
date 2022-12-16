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

import com.codahale.metrics.Meter;

import java.util.concurrent.atomic.AtomicLong;

public class Rate implements IMetric {
  AtomicLong atomicLong;
  Meter meter;

  public Rate(AtomicLong atomicLong) {
    this.atomicLong = atomicLong;
    this.meter = new Meter();
  }

  public long getCount() {
    return meter.getCount();
  }

  public double getOneMinuteRate() {
    return meter.getOneMinuteRate();
  }

  public double getMeanRate() {
    return meter.getMeanRate();
  }

  public double getFiveMinuteRate() {
    return meter.getFiveMinuteRate();
  }

  public double getFifteenMinuteRate() {
    return meter.getFifteenMinuteRate();
  }

  public void mark() {
    atomicLong.set(1);
    meter.mark();
  }

  public void mark(long n) {
    atomicLong.set(n);
    meter.mark(n);
  }
}
