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

package com.spricoder.ddbs.metric;

import com.spricoder.ddbs.metric.metricsets.JvmMetrics;
import com.spricoder.ddbs.metric.metricsets.ProcessMetrics;
import com.spricoder.ddbs.metric.metricsets.SystemMetrics;
import com.spricoder.ddbs.metric.type.AutoGauge;
import com.spricoder.ddbs.metric.type.Counter;
import com.spricoder.ddbs.metric.type.Gauge;
import com.spricoder.ddbs.metric.type.Histogram;
import com.spricoder.ddbs.metric.type.HistogramSnapshot;
import com.spricoder.ddbs.metric.type.IMetric;
import com.spricoder.ddbs.metric.type.Rate;
import com.spricoder.ddbs.metric.type.Timer;
import com.spricoder.ddbs.metric.utils.MetricInfo;
import com.spricoder.ddbs.metric.utils.MetricType;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.stereotype.Component;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.ToLongFunction;
import javafx.util.Pair;

@Component
public class MetricService {
  io.micrometer.core.instrument.MeterRegistry meterRegistry;
  protected Map<String, MetricInfo.MetaInfo> nameToMetaInfo;
  protected Map<MetricInfo, IMetric> metrics;

  public MetricService() {
    meterRegistry = Metrics.globalRegistry;
    Metrics.globalRegistry.add(new SimpleMeterRegistry());
    nameToMetaInfo = new ConcurrentHashMap<>();
    metrics = new ConcurrentHashMap<>();
    new JvmMetrics().bindTo(this);
    new ProcessMetrics().bindTo(this);
    new SystemMetrics().bindTo(this);
  }

  // region create metric

  public Counter getOrCreateCounter(String name, String... tags) {
    MetricInfo metricInfo = new MetricInfo(MetricType.COUNTER, name, tags);
    IMetric metric =
        metrics.computeIfAbsent(
            metricInfo,
            key -> {
              Counter counter =
                  new Counter(
                      meterRegistry.counter(metricInfo.getName(), metricInfo.getTagsInArray()));
              nameToMetaInfo.put(name, metricInfo.getMetaInfo());
              return counter;
            });
    if (metric instanceof Counter) {
      return (Counter) metric;
    }
    throw new IllegalArgumentException(
        metricInfo + " is already used for a different type of name");
  }

  public <T> AutoGauge createAutoGauge(
      String name, T obj, ToLongFunction<T> mapper, String... tags) {
    MetricInfo metricInfo = new MetricInfo(MetricType.AUTO_GAUGE, name, tags);
    AutoGauge gauge =
        new AutoGauge<T>(
            meterRegistry, metricInfo.getName(), obj, mapper, metricInfo.getTagsInArray());
    nameToMetaInfo.put(name, metricInfo.getMetaInfo());
    metrics.put(metricInfo, gauge);
    return gauge;
  }

  public <T> AutoGauge getAutoGauge(String name, String... tags) {
    MetricInfo metricInfo = new MetricInfo(MetricType.AUTO_GAUGE, name, tags);
    IMetric metric = metrics.get(metricInfo);
    if (metric instanceof AutoGauge) {
      return (AutoGauge) metric;
    }
    throw new IllegalArgumentException(
        metricInfo + " is already used for a different type of name");
  }

  public Gauge getOrCreateGauge(String name, String... tags) {
    MetricInfo metricInfo = new MetricInfo(MetricType.GAUGE, name, tags);
    IMetric metric =
        metrics.computeIfAbsent(
            metricInfo,
            key -> {
              Gauge gauge =
                  new Gauge(meterRegistry, metricInfo.getName(), metricInfo.getTagsInArray());
              nameToMetaInfo.put(name, metricInfo.getMetaInfo());
              return gauge;
            });
    if (metric instanceof Gauge) {
      return (Gauge) metric;
    }
    throw new IllegalArgumentException(
        metricInfo + " is already used for a different type of name");
  }

  public Rate getOrCreateRate(String name, String... tags) {
    MetricInfo metricInfo = new MetricInfo(MetricType.RATE, name, tags);
    IMetric metric =
        metrics.computeIfAbsent(
            metricInfo,
            key -> {
              Rate rate =
                  new Rate(
                      meterRegistry.gauge(
                          metricInfo.getName(),
                          Tags.of(metricInfo.getTagsInArray()),
                          new AtomicLong(0)));
              nameToMetaInfo.put(name, metricInfo.getMetaInfo());
              return rate;
            });
    if (metric instanceof Rate) {
      return (Rate) metric;
    }
    throw new IllegalArgumentException(
        metricInfo + " is already used for a different type of name");
  }

  public Histogram getOrCreateHistogram(String name, String... tags) {
    MetricInfo metricInfo = new MetricInfo(MetricType.HISTOGRAM, name, tags);
    IMetric metric =
        metrics.computeIfAbsent(
            metricInfo,
            key -> {
              io.micrometer.core.instrument.DistributionSummary distributionSummary =
                  io.micrometer.core.instrument.DistributionSummary.builder(metricInfo.getName())
                      .tags(metricInfo.getTagsInArray())
                      .publishPercentiles(0, 0.25, 0.5, 0.75, 1)
                      .register(meterRegistry);
              Histogram histogram = new Histogram(distributionSummary);
              nameToMetaInfo.put(name, metricInfo.getMetaInfo());
              return histogram;
            });
    if (metric instanceof Histogram) {
      return (Histogram) metric;
    }
    throw new IllegalArgumentException(
        metricInfo + " is already used for a different type of name");
  }

  public Timer getOrCreateTimer(String name, String... tags) {
    MetricInfo metricInfo = new MetricInfo(MetricType.TIMER, name, tags);
    IMetric metric =
        metrics.computeIfAbsent(
            metricInfo,
            key -> {
              io.micrometer.core.instrument.Timer timer =
                  io.micrometer.core.instrument.Timer.builder(metricInfo.getName())
                      .tags(metricInfo.getTagsInArray())
                      .publishPercentiles(0, 0.25, 0.5, 0.75, 1)
                      .register(meterRegistry);
              Timer timerResult = new Timer(timer);
              nameToMetaInfo.put(name, metricInfo.getMetaInfo());
              return timerResult;
            });
    if (metric instanceof Timer) {
      return (Timer) metric;
    }
    throw new IllegalArgumentException(
        metricInfo + " is already used for a different type of name");
  }

  // endregion

  // region update metric

  public Counter count(long delta, String name, String... tags) {
    Counter counter = getOrCreateCounter(name, tags);
    counter.inc(delta);
    return counter;
  }

  public Gauge gauge(long value, String name, String... tags) {
    Gauge gauge = getOrCreateGauge(name, tags);
    gauge.set(value);
    return gauge;
  }

  public Rate rate(long value, String name, String... tags) {
    Rate rate = getOrCreateRate(name, tags);
    rate.mark(value);
    return rate;
  }

  public Histogram histogram(long value, String name, String... tags) {
    Histogram histogram = getOrCreateHistogram(name, tags);
    histogram.update(value);
    return histogram;
  }

  public Timer timer(long delta, TimeUnit timeUnit, String name, String... tags) {
    Timer timer = getOrCreateTimer(name, tags);
    timer.update(delta, timeUnit);
    return timer;
  }

  // endregion

  // region get metric

  public List<Pair<String, String[]>> getAllMetricKeys() {
    List<Pair<String, String[]>> keys = new ArrayList<>(metrics.size());
    metrics.keySet().forEach(k -> keys.add(k.toStringArray()));
    return keys;
  }

  public Map<MetricInfo, IMetric> getAllMetrics() {
    return metrics;
  }

  public Map<MetricInfo, IMetric> getMetricsByType(MetricType metricType) {
    Map<MetricInfo, IMetric> result = new HashMap<>();
    for (Map.Entry<MetricInfo, IMetric> entry : metrics.entrySet()) {
      if (entry.getKey().getMetaInfo().getType() == metricType) {
        result.put(entry.getKey(), entry.getValue());
      }
    }
    return result;
  }

  // endregion

  // region remove metric

  public void remove(MetricType type, String name, String... tags) {
    MetricInfo metricInfo = new MetricInfo(type, name, tags);
    if (metrics.containsKey(metricInfo)) {
      if (type == metricInfo.getMetaInfo().getType()) {
        nameToMetaInfo.remove(metricInfo.getName());
        metrics.remove(metricInfo);
        removeMetric(type, metricInfo);
      } else {
        throw new IllegalArgumentException(
            metricInfo + " failed to remove because the mismatch of type. ");
      }
    }
  }

  protected void removeMetric(MetricType type, MetricInfo metricInfo) {
    Meter.Type meterType = transformType(type);
    Meter.Id id =
        new Meter.Id(
            metricInfo.getName(), Tags.of(metricInfo.getTagsInArray()), null, null, meterType);
    meterRegistry.remove(id);
  }

  // endregion

  // region report
  public String scrape() {
    Writer writer = new StringWriter();
    PrometheusTextWriter prometheusTextWriter = new PrometheusTextWriter(writer);

    String result;
    try {
      for (Map.Entry<MetricInfo, IMetric> metricEntry : getAllMetrics().entrySet()) {
        MetricInfo metricInfo = metricEntry.getKey();
        IMetric metric = metricEntry.getValue();

        String name = metricInfo.getName().replaceAll("[^a-zA-Z0-9:_\\]\\[]", "_");
        MetricType metricType = metricInfo.getMetaInfo().getType();
        if (metric instanceof Counter) {
          name += "_total";
          prometheusTextWriter.writeHelp(name);
          prometheusTextWriter.writeType(name, metricInfo.getMetaInfo().getType());
          Counter counter = (Counter) metric;
          prometheusTextWriter.writeSample(name, metricInfo.getTags(), counter.count());
        } else if (metric instanceof Gauge) {
          prometheusTextWriter.writeHelp(name);
          prometheusTextWriter.writeType(name, metricInfo.getMetaInfo().getType());
          Gauge gauge = (Gauge) metric;
          prometheusTextWriter.writeSample(name, metricInfo.getTags(), gauge.value());
        } else if (metric instanceof AutoGauge) {
          prometheusTextWriter.writeHelp(name);
          prometheusTextWriter.writeType(name, metricInfo.getMetaInfo().getType());
          AutoGauge gauge = (AutoGauge) metric;
          prometheusTextWriter.writeSample(name, metricInfo.getTags(), gauge.value());
        } else if (metric instanceof Histogram) {
          Histogram histogram = (Histogram) metric;
          HistogramSnapshot snapshot = histogram.takeSnapshot();
          writeSnapshotAndCount(
              name,
              metricInfo.getTags(),
              metricType,
              snapshot,
              histogram.count(),
              prometheusTextWriter);
        } else if (metric instanceof Rate) {
          name += "_total";
          prometheusTextWriter.writeHelp(name);
          prometheusTextWriter.writeType(name, metricInfo.getMetaInfo().getType());
          Rate rate = (Rate) metric;
          prometheusTextWriter.writeSample(name, metricInfo.getTags(), rate.getCount());
          prometheusTextWriter.writeSample(
              name, addTags(metricInfo.getTags(), "rate", "m1"), rate.getOneMinuteRate());
          prometheusTextWriter.writeSample(
              name, addTags(metricInfo.getTags(), "rate", "m5"), rate.getFiveMinuteRate());
          prometheusTextWriter.writeSample(
              name, addTags(metricInfo.getTags(), "rate", "m15"), rate.getFifteenMinuteRate());
          prometheusTextWriter.writeSample(
              name, addTags(metricInfo.getTags(), "rate", "mean"), rate.getMeanRate());
        } else if (metric instanceof Timer) {
          Timer timer = (Timer) metric;
          HistogramSnapshot snapshot = timer.takeSnapshot();
          name += "_seconds";
          writeSnapshotAndCount(
              name,
              metricInfo.getTags(),
              metricType,
              snapshot,
              timer.getImmutableRate().getCount(),
              prometheusTextWriter);
        }
      }
      result = writer.toString();
    } catch (IOException e) {
      // This actually never happens since StringWriter::write() doesn't throw any IOException
      throw new RuntimeException(e);
    } finally {
      try {
        writer.close();
      } catch (IOException e) {
        // do nothing
      }
    }
    return result;
  }

  private void writeSnapshotAndCount(
      String name,
      Map<String, String> tags,
      MetricType type,
      HistogramSnapshot snapshot,
      long count,
      PrometheusTextWriter prometheusTextWriter)
      throws IOException {
    prometheusTextWriter.writeHelp(name);
    prometheusTextWriter.writeType(name, type);
    prometheusTextWriter.writeSample(name + "_max", tags, snapshot.getMax());
    prometheusTextWriter.writeSample(
        name + "_sum", tags, Arrays.stream(snapshot.getValues()).sum());
    prometheusTextWriter.writeSample(name + "_count", tags, count);

    prometheusTextWriter.writeSample(
        name, addTags(tags, "quantile", "0.0"), snapshot.getValue(0.0));
    prometheusTextWriter.writeSample(
        name, addTags(tags, "quantile", "0.25"), snapshot.getValue(0.25));
    prometheusTextWriter.writeSample(
        name, addTags(tags, "quantile", "0.5"), snapshot.getValue(0.5));
    prometheusTextWriter.writeSample(
        name, addTags(tags, "quantile", "0.75"), snapshot.getValue(0.75));
    prometheusTextWriter.writeSample(
        name, addTags(tags, "quantile", "1.0"), snapshot.getValue(1.0));
  }

  private Map<String, String> addTags(Map<String, String> tags, String key, String value) {
    HashMap<String, String> result = new HashMap<>(tags);
    result.put(key, value);
    return result;
  }

  public class PrometheusTextWriter extends FilterWriter {

    public PrometheusTextWriter(Writer out) {
      super(out);
    }

    public void writeHelp(String name) throws IOException {
      write("# HELP ");
      write(name);
      write('\n');
    }

    public void writeType(String name, MetricType type) throws IOException {
      write("# TYPE ");
      write(name);
      write(' ');
      switch (type) {
        case GAUGE:
        case AUTO_GAUGE:
          write("gauge");
          break;
        case COUNTER:
        case RATE:
          write("counter");
          break;
        case TIMER:
        case HISTOGRAM:
          write("summary");
          break;
        default:
          break;
      }
      write('\n');
    }

    public void writeSample(String name, Map<String, String> labels, Object value)
        throws IOException {
      write(name);
      if (labels.size() > 0) {
        write('{');
        for (Map.Entry<String, String> entry : labels.entrySet()) {
          write(entry.getKey());
          write("=\"");
          write(entry.getValue());
          write("\",");
        }
        write('}');
      }
      write(' ');
      write(value.toString());
      write('\n');
    }
  }

  // endregion

  private Meter.Type transformType(MetricType type) {
    switch (type) {
      case COUNTER:
        return Meter.Type.COUNTER;
      case AUTO_GAUGE:
      case GAUGE:
      case RATE:
        return Meter.Type.GAUGE;
      case HISTOGRAM:
        return Meter.Type.DISTRIBUTION_SUMMARY;
      case TIMER:
        return Meter.Type.TIMER;
      default:
        return Meter.Type.OTHER;
    }
  }
}
