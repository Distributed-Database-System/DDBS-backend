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

public class ProcessMetrics implements IMetricSet {
  private final OperatingSystemMXBean sunOsMXBean =
      (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
  private final Runtime runtime = Runtime.getRuntime();

  @Override
  public void bindTo(MetricService metricService) {
    collectProcessCPUInfo(metricService);
    collectProcessMemInfo(metricService);
    collectProcessStatusInfo(metricService);
    collectThreadInfo(metricService);
  }

  @Override
  public void unbindFrom(MetricService metricService) {
    removeProcessCPUInfo(metricService);
    removeProcessMemInfo(metricService);
    removeProcessStatusInfo(metricService);
    removeThreadInfo(metricService);
  }

  private void collectProcessCPUInfo(MetricService metricService) {
    metricService.createAutoGauge(
        Metric.PROCESS_CPU_LOAD.toString(),
        sunOsMXBean,
        a -> (long) (sunOsMXBean.getProcessCpuLoad() * 100),
        Tag.NAME.toString(),
        "process");

    metricService.createAutoGauge(
        Metric.PROCESS_CPU_TIME.toString(),
        sunOsMXBean,
        com.sun.management.OperatingSystemMXBean::getProcessCpuTime,
        Tag.NAME.toString(),
        "process");
  }

  private void removeProcessCPUInfo(MetricService metricService) {
    metricService.remove(
        MetricType.AUTO_GAUGE, Metric.PROCESS_CPU_LOAD.toString(), Tag.NAME.toString(), "process");

    metricService.remove(
        MetricType.AUTO_GAUGE, Metric.PROCESS_CPU_TIME.toString(), Tag.NAME.toString(), "process");
  }

  private void collectProcessMemInfo(MetricService metricService) {
    Runtime runtime = Runtime.getRuntime();
    metricService.createAutoGauge(
        Metric.PROCESS_MAX_MEM.toString(),
        runtime,
        a -> runtime.maxMemory(),
        Tag.NAME.toString(),
        "process");
    metricService.createAutoGauge(
        Metric.PROCESS_TOTAL_MEM.toString(),
        runtime,
        a -> runtime.totalMemory(),
        Tag.NAME.toString(),
        "process");
    metricService.createAutoGauge(
        Metric.PROCESS_FREE_MEM.toString(),
        runtime,
        a -> runtime.freeMemory(),
        Tag.NAME.toString(),
        "process");
    // TODO maybe following metrics can be removed
    metricService.createAutoGauge(
        Metric.PROCESS_USED_MEM.toString(),
        this,
        a -> getProcessUsedMemory(),
        Tag.NAME.toString(),
        "process");
    metricService.createAutoGauge(
        Metric.PROCESS_MEM_RATIO.toString(),
        this,
        a -> Math.round(getProcessMemoryRatio()),
        Tag.NAME.toString(),
        "process");
  }

  private void removeProcessMemInfo(MetricService metricService) {
    metricService.remove(
        MetricType.AUTO_GAUGE, Metric.PROCESS_MAX_MEM.toString(), Tag.NAME.toString(), "process");
    metricService.remove(
        MetricType.AUTO_GAUGE, Metric.PROCESS_TOTAL_MEM.toString(), Tag.NAME.toString(), "process");
    metricService.remove(
        MetricType.AUTO_GAUGE, Metric.PROCESS_FREE_MEM.toString(), Tag.NAME.toString(), "process");
    metricService.remove(
        MetricType.AUTO_GAUGE, Metric.PROCESS_USED_MEM.toString(), Tag.NAME.toString(), "process");
    metricService.remove(
        MetricType.AUTO_GAUGE, Metric.PROCESS_MEM_RATIO.toString(), Tag.NAME.toString(), "process");
  }

  private void collectThreadInfo(MetricService metricService) {
    // TODO maybe duplicated with thread info in jvm related metrics
    metricService.createAutoGauge(
        Metric.PROCESS_THREADS_COUNT.toString(),
        this,
        a -> getThreadsCount(),
        Tag.NAME.toString(),
        "process");
  }

  private void removeThreadInfo(MetricService metricService) {
    metricService.remove(
        MetricType.AUTO_GAUGE,
        Metric.PROCESS_THREADS_COUNT.toString(),
        Tag.NAME.toString(),
        "process");
  }

  private void collectProcessStatusInfo(MetricService metricService) {
    metricService.createAutoGauge(
        Metric.PROCESS_STATUS.toString(),
        this,
        a -> (getProcessStatus()),
        Tag.NAME.toString(),
        "process");
  }

  private void removeProcessStatusInfo(MetricService metricService) {
    metricService.remove(
        MetricType.AUTO_GAUGE, Metric.PROCESS_STATUS.toString(), Tag.NAME.toString(), "process");
  }

  private long getProcessUsedMemory() {
    return runtime.totalMemory() - runtime.freeMemory();
  }

  private long getProcessStatus() {
    return Thread.currentThread().isAlive() ? 1 : 0;
  }

  private int getThreadsCount() {
    ThreadGroup parentThread;
    for (parentThread = Thread.currentThread().getThreadGroup();
        parentThread.getParent() != null;
        parentThread = parentThread.getParent()) {}

    return parentThread.activeCount();
  }

  private double getProcessMemoryRatio() {
    long processUsedMemory = getProcessUsedMemory();
    long totalPhysicalMemorySize = sunOsMXBean.getTotalPhysicalMemorySize();
    return (double) processUsedMemory / (double) totalPhysicalMemorySize * 100;
  }
}
