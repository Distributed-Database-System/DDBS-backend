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

package com.spricoder.ddbs.metric.metricsets;

import com.spricoder.ddbs.metric.MetricService;
import com.spricoder.ddbs.metric.utils.Metric;
import com.spricoder.ddbs.metric.utils.MetricType;
import com.spricoder.ddbs.metric.utils.Tag;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public class SystemMetrics implements IMetricSet {
  private com.sun.management.OperatingSystemMXBean osMXBean =
      (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

  @Override
  public void bindTo(MetricService metricService) {
    collectSystemCpuInfo(metricService);
    collectSystemMemInfo(metricService);
  }

  @Override
  public void unbindFrom(MetricService metricService) {
    removeSystemCpuInfo(metricService);
    removeSystemMemInfo(metricService);
  }

  private void collectSystemCpuInfo(MetricService metricService) {
    metricService.createAutoGauge(
        Metric.SYS_CPU_LOAD.toString(),
        osMXBean,
        a -> (long) (osMXBean.getSystemCpuLoad() * 100),
        Tag.NAME.toString(),
        "system");

    metricService
        .getOrCreateGauge(Metric.SYS_CPU_CORES.toString(), Tag.NAME.toString(), "system")
        .set(osMXBean.getAvailableProcessors());
  }

  private void removeSystemCpuInfo(MetricService metricService) {
    metricService.remove(
        MetricType.AUTO_GAUGE, Metric.SYS_CPU_LOAD.toString(), Tag.NAME.toString(), "system");

    metricService.remove(
        MetricType.GAUGE, Metric.SYS_CPU_CORES.toString(), Tag.NAME.toString(), "system");
  }

  private void collectSystemMemInfo(MetricService metricService) {
    metricService
        .getOrCreateGauge(
            Metric.SYS_TOTAL_PHYSICAL_MEMORY_SIZE.toString(), Tag.NAME.toString(), "system")
        .set(osMXBean.getTotalPhysicalMemorySize());
    metricService.createAutoGauge(
        Metric.SYS_FREE_PHYSICAL_MEMORY_SIZE.toString(),
        osMXBean,
        a -> osMXBean.getFreePhysicalMemorySize(),
        Tag.NAME.toString(),
        "system");
    metricService.createAutoGauge(
        Metric.SYS_TOTAL_SWAP_SPACE_SIZE.toString(),
        osMXBean,
        a -> osMXBean.getTotalSwapSpaceSize(),
        Tag.NAME.toString(),
        "system");
    metricService.createAutoGauge(
        Metric.SYS_FREE_SWAP_SPACE_SIZE.toString(),
        osMXBean,
        a -> osMXBean.getFreeSwapSpaceSize(),
        Tag.NAME.toString(),
        "system");
    metricService.createAutoGauge(
        Metric.SYS_COMMITTED_VM_SIZE.toString(),
        osMXBean,
        a -> osMXBean.getCommittedVirtualMemorySize(),
        Tag.NAME.toString(),
        "system");
  }

  private void removeSystemMemInfo(MetricService metricService) {
    metricService.remove(
        MetricType.GAUGE,
        Metric.SYS_TOTAL_PHYSICAL_MEMORY_SIZE.toString(),
        Tag.NAME.toString(),
        "system");
    metricService.remove(
        MetricType.AUTO_GAUGE,
        Metric.SYS_FREE_PHYSICAL_MEMORY_SIZE.toString(),
        Tag.NAME.toString(),
        "system");
    metricService.remove(
        MetricType.AUTO_GAUGE,
        Metric.SYS_TOTAL_SWAP_SPACE_SIZE.toString(),
        Tag.NAME.toString(),
        "system");
    metricService.remove(
        MetricType.AUTO_GAUGE,
        Metric.SYS_FREE_SWAP_SPACE_SIZE.toString(),
        Tag.NAME.toString(),
        "system");
    metricService.remove(
        MetricType.AUTO_GAUGE,
        Metric.SYS_COMMITTED_VM_SIZE.toString(),
        Tag.NAME.toString(),
        "system");
  }
}
