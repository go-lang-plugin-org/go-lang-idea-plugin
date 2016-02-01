/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.runconfig.testing.frameworks.gobench;

import com.goide.runconfig.testing.GoTestLocationProvider;
import com.google.common.base.Stopwatch;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.execution.testframework.sm.runner.events.TestFailedEvent;
import com.intellij.execution.testframework.sm.runner.events.TestFinishedEvent;
import com.intellij.execution.testframework.sm.runner.events.TestStartedEvent;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GobenchEventsConverter extends OutputToGeneralTestEventsConverter {
  private static final Pattern RUN = Pattern.compile("^(Benchmark[^ (\n\t\r]+)");
  private static final Pattern FAIL = Pattern.compile("^--- FAIL: (Benchmark[^ (\n\t\r]+)");
  private static final Pattern OK = Pattern.compile("^ok  \t");

  private final Stopwatch testStopwatch = Stopwatch.createUnstarted();
  private String currentBenchmark = "<benchmark>";
  private boolean benchmarkFailing = false;
  
  public GobenchEventsConverter(TestConsoleProperties properties) {
    super(GobenchFramework.NAME, properties);
  }

  @Override
  public void process(String text, Key outputType) {
    Matcher matcher;

    if (OK.matcher(text).find()) {
      maybeFinishCurrentTest();
    } else if ((matcher = RUN.matcher(text)).find()) {
      maybeFinishCurrentTest();
      testStopwatch.start();
      currentBenchmark = StringUtil.notNullize(matcher.group(1), "<benchmark>");
      TestStartedEvent event = new TestStartedEvent(currentBenchmark, testUrl(currentBenchmark));
      getProcessor().onTestStarted(event);
    } else if ((matcher = FAIL.matcher(text)).find()) {
      currentBenchmark = StringUtil.notNullize(matcher.group(1), "<benchmark>");
      benchmarkFailing = true;
    }

    super.process(text, outputType);
  }

  private void maybeFinishCurrentTest() {
    if (testStopwatch.isRunning()) {
      testStopwatch.stop();
      if (benchmarkFailing) {
        TestFailedEvent event = new TestFailedEvent(currentBenchmark, "", null, true, null, null);
        getProcessor().onTestFailure(event);
        benchmarkFailing = false;
      }

      TestFinishedEvent event = new TestFinishedEvent(currentBenchmark, testStopwatch.elapsed(TimeUnit.MILLISECONDS));
      testStopwatch.reset();
      getProcessor().onTestFinished(event);
    }
  }

  @NotNull
  private static String testUrl(@NotNull String testName) {
    return GoTestLocationProvider.PROTOCOL + "://" + testName;
  }
}
