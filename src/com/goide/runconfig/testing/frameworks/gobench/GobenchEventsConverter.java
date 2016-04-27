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

import com.goide.GoConstants;
import com.goide.runconfig.testing.GoTestEventsConverterBase;
import com.goide.runconfig.testing.GoTestLocator;
import com.google.common.base.Stopwatch;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GobenchEventsConverter extends OutputToGeneralTestEventsConverter implements GoTestEventsConverterBase {
  private static final Pattern RUN = Pattern.compile("^(Benchmark" + GoConstants.IDENTIFIER_REGEX + ")");
  private static final Pattern FAIL = Pattern.compile("^--- FAIL: (Benchmark" + GoConstants.IDENTIFIER_REGEX + ").*");
  private static final Pattern TAIL = Pattern.compile("\\S*[ \t]+(.+)");
  private static final String TOP_LEVEL_BENCHMARK_NAME = "<benchmark>";

  private final Stopwatch testStopwatch = Stopwatch.createUnstarted();
  private String currentBenchmark = TOP_LEVEL_BENCHMARK_NAME;
  private boolean benchmarkFailing;
  private ServiceMessageVisitor myVisitor;

  public GobenchEventsConverter(TestConsoleProperties properties) {
    super(GobenchFramework.NAME, properties);
  }

  @Override
  public boolean processServiceMessages(@NotNull String text, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
    if (myVisitor == null && visitor != null) {
      myVisitor = visitor;
    }
    if (text.isEmpty()) {
      return true;
    }
    
    Matcher matcher;
    if ((matcher = RUN.matcher(text)).find()) {
      if (testStopwatch.isRunning()) {
        finishCurrentTest(outputType, visitor);
      }
      testStopwatch.start();
      currentBenchmark = StringUtil.notNullize(matcher.group(1), TOP_LEVEL_BENCHMARK_NAME);

      String testStartedMessage = ServiceMessageBuilder.testStarted(currentBenchmark)
        .addAttribute("locationHint", testUrl(currentBenchmark)).toString();
      super.processServiceMessages(testStartedMessage, outputType, visitor);
      processTailText(text, outputType, visitor, matcher.end(1));
      return true;
    }

    if ((matcher = FAIL.matcher(text)).find()) {
      String failedBenchmark = StringUtil.notNullize(matcher.group(1), TOP_LEVEL_BENCHMARK_NAME);
      if (!failedBenchmark.equals(currentBenchmark)) {
        finishCurrentTest(outputType, visitor);
      }
      currentBenchmark = failedBenchmark;
      benchmarkFailing = true;
      processTailText(text, outputType, visitor, matcher.end(1));
      return true;
    }

    boolean isErrorMessage = ProcessOutputTypes.STDERR == outputType;
    if (isTestStarted()) {
      ServiceMessageBuilder builder = isErrorMessage ? ServiceMessageBuilder.testStdErr(currentBenchmark)
                                                     : ServiceMessageBuilder.testStdOut(currentBenchmark);
      super.processServiceMessages(builder.addAttribute("out", text).toString(), outputType, visitor);
      return true;
    }

    ServiceMessageBuilder messageBuilder = new ServiceMessageBuilder(ServiceMessageTypes.MESSAGE);
    if (isErrorMessage) {
      messageBuilder.addAttribute("text", StringUtil.trimEnd(text, "\n")).addAttribute("status", "ERROR");
    }
    else {
      messageBuilder.addAttribute("text", text).addAttribute("status", "NORMAL");
    }
    super.processServiceMessages(messageBuilder.toString(), outputType, visitor);
    return true;
  }

  @Override
  public void flushBufferBeforeTerminating() {
    try {
      finishCurrentTest(null, myVisitor);
    }
    catch (ParseException ignore) {}
    myVisitor = null;
    super.flushBufferBeforeTerminating();
  }

  private boolean isTestStarted() {return !TOP_LEVEL_BENCHMARK_NAME.equals(currentBenchmark);}

  private void processTailText(@NotNull String text, Key outputType, ServiceMessageVisitor visitor, int tailOffset)
    throws ParseException {
    String tailText = text.substring(tailOffset);
    Matcher tailMatcher = TAIL.matcher(tailText);
    if (!tailText.isEmpty() && tailMatcher.find())  {
      processServiceMessages(tailText.substring(tailMatcher.start(1)), outputType, visitor);
    }
  }

  private void finishCurrentTest(@Nullable Key outputType, @Nullable ServiceMessageVisitor visitor) throws ParseException {
    if (isTestStarted()) {
      testStopwatch.stop();
      if (benchmarkFailing) {
        benchmarkFailing = false;
        String failedMessage = ServiceMessageBuilder.testFailed(currentBenchmark).addAttribute("message", "").toString();
        super.processServiceMessages(failedMessage, outputType, visitor);
      }

      String finishedMessage = ServiceMessageBuilder.testFinished(currentBenchmark)
        .addAttribute("duration", Long.toString(testStopwatch.elapsed(TimeUnit.MILLISECONDS))).toString();
      testStopwatch.reset();
      super.processServiceMessages(finishedMessage, outputType, visitor);
    }
  }

  @NotNull
  private static String testUrl(@NotNull String testName) {
    return GoTestLocator.PROTOCOL + "://" + testName;
  }
}