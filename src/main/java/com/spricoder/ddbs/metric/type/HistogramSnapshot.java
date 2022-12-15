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

import java.util.Arrays;

public class HistogramSnapshot implements IMetric {
  io.micrometer.core.instrument.distribution.HistogramSnapshot histogramSnapshot;

  public HistogramSnapshot(
      io.micrometer.core.instrument.distribution.HistogramSnapshot histogramSnapshot) {
    this.histogramSnapshot = histogramSnapshot;
  }

  public double getValue(double quantile) {
    int prevIndex = 0;
    for (int i = 0; i < this.histogramSnapshot.percentileValues().length; i++) {
      if (this.histogramSnapshot.percentileValues()[i].percentile() <= quantile) {
        prevIndex = i;
      }
      if (this.histogramSnapshot.percentileValues()[i].percentile() >= quantile) {
        // Find the value of the first matching or most suitable insertion position
        break;
      }
    }

    return this.histogramSnapshot.percentileValues()[prevIndex].value();
  }

  public long[] getValues() {
    return Arrays.stream(this.histogramSnapshot.percentileValues())
        .mapToLong(k -> (long) k.value())
        .toArray();
  }

  public int size() {
    return this.histogramSnapshot.percentileValues().length;
  }

  public double getMedian() {
    return getValue(0.5);
  }

  public long getMax() {
    return (long) this.histogramSnapshot.max();
  }

  public double getMean() {
    return this.histogramSnapshot.mean();
  }

  public long getMin() {
    // need distributionSummary to push 0 percentiles
    return (long) getValue(0.0);
  }
}
