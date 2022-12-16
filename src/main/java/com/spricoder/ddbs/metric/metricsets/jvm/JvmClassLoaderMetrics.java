/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.spricoder.ddbs.metric.metricsets.jvm;

import com.spricoder.ddbs.metric.MetricService;
import com.spricoder.ddbs.metric.metricsets.IMetricSet;
import com.spricoder.ddbs.metric.utils.MetricType;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

/** This file is modified from io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics */
public class JvmClassLoaderMetrics implements IMetricSet {
  @Override
  public void bindTo(MetricService metricService) {
    ClassLoadingMXBean classLoadingBean = ManagementFactory.getClassLoadingMXBean();
    metricService.createAutoGauge(
        "jvm_classes_loaded_classes", classLoadingBean, ClassLoadingMXBean::getLoadedClassCount);
    metricService.createAutoGauge(
        "jvm_classes_unloaded_classes",
        classLoadingBean,
        ClassLoadingMXBean::getUnloadedClassCount);
  }

  @Override
  public void unbindFrom(MetricService metricService) {
    metricService.remove(MetricType.AUTO_GAUGE, "jvm_classes_loaded_classes");
    metricService.remove(MetricType.AUTO_GAUGE, "jvm_classes_unloaded_classes");
  }
}
