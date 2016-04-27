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

package com.goide.runconfig.testing.frameworks.gotest;

import com.goide.runconfig.testing.GoTestEventsConverterBase;
import com.goide.runconfig.testing.GoTestLocator;
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

public abstract class GoTestEventsConverterBaseImpl extends OutputToGeneralTestEventsConverter implements GoTestEventsConverterBase {
  public enum TestResult {PASSED, FAILED, SKIPPED}

  @Nullable
  private ServiceMessageVisitor myVisitor;
  @Nullable
  private String myCurrentTestName;
  @Nullable
  private TestResult myCurrentTestResult;
  private long myCurrentTestStart;

  public GoTestEventsConverterBaseImpl(@NotNull String testFrameworkName, @NotNull TestConsoleProperties consoleProperties) {
    super(testFrameworkName, consoleProperties);
  }

  protected abstract int processLine(@NotNull String line, int start, Key outputType, ServiceMessageVisitor visitor)
    throws ParseException;
  
  @Nullable
  protected String getCurrentTestName() {
    return myCurrentTestName;
  }

  @Override
  public final boolean processServiceMessages(@NotNull String text, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
    if (myVisitor == null && visitor != null) {
      myVisitor = visitor;
    }
    if (text.isEmpty()) {
      return true;
    }

    int startOffset = 0;
    int newStartOffset = processLine(text, startOffset, outputType, visitor);
    while (startOffset != newStartOffset && newStartOffset < text.length()) {
      startOffset = newStartOffset;
      newStartOffset = processLine(text, startOffset, outputType, visitor);
    }
    if (newStartOffset < text.length()) {
      processOutput(text.substring(startOffset), outputType, visitor);
    }
    return true;
  }

  protected void processOutput(@NotNull String text, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
    if (text.isEmpty()) {
      return;
    }
    boolean isErrorMessage = ProcessOutputTypes.STDERR == outputType;
    if (myCurrentTestName != null) {
      ServiceMessageBuilder builder = isErrorMessage ? ServiceMessageBuilder.testStdErr(myCurrentTestName)
                                                     : ServiceMessageBuilder.testStdOut(myCurrentTestName);
      super.processServiceMessages(builder.addAttribute("out", text).toString(), outputType, visitor);
      return;
    }

    ServiceMessageBuilder messageBuilder = new ServiceMessageBuilder(ServiceMessageTypes.MESSAGE);
    if (isErrorMessage) {
      messageBuilder.addAttribute("text", StringUtil.trimEnd(text, "\n")).addAttribute("status", "ERROR");
    }
    else {
      messageBuilder.addAttribute("text", text).addAttribute("status", "NORMAL");
    }
    super.processServiceMessages(messageBuilder.toString(), outputType, visitor);
  }

  @Override
  public void dispose() {
    myVisitor = null;
    super.dispose();
  }

  protected void startTest(@NotNull String testName, @Nullable ServiceMessageVisitor visitor) throws ParseException {
    if (isCurrentlyRunningTest(testName)) {
      return;
    }
    finishDelayedTest(visitor);
    myCurrentTestName = testName;
    myCurrentTestResult = null;
    myCurrentTestStart = System.currentTimeMillis();

    String testStartedMessage = ServiceMessageBuilder.testStarted(testName).addAttribute("locationHint", testUrl(testName)).toString();
    super.processServiceMessages(testStartedMessage, null, visitor);
  }

  @Override
  public final void flushBufferBeforeTerminating() {
    try {
      if (!finishDelayedTest(myVisitor)) {
        if (myCurrentTestName != null) {
          finishTestInner(myCurrentTestName, TestResult.PASSED, myVisitor);
        }
      }
    }
    catch (ParseException ignore) {
    }
    myVisitor = null;
    super.flushBufferBeforeTerminating();
  }

  protected void finishTest(@NotNull String name, @NotNull TestResult result, @Nullable ServiceMessageVisitor visitor)
    throws ParseException {
    if (isCurrentlyRunningTest(name)) {
      if (myCurrentTestResult == null) {
        // delay finishing test, we'll use it as a container for future output
        myCurrentTestResult = result;
      }
      return;
    }
    finishTestInner(name, result, visitor);
  }

  protected boolean finishDelayedTest(ServiceMessageVisitor visitor) throws ParseException {
    if (myCurrentTestName != null && myCurrentTestResult != null) {
      finishTestInner(myCurrentTestName, myCurrentTestResult, visitor);
      return true;
    }
    return false;
  }

  private boolean isCurrentlyRunningTest(@NotNull String testName) {
    return testName.equals(myCurrentTestName);
  }

  private void finishTestInner(@NotNull String name,
                               @NotNull TestResult result,
                               @Nullable ServiceMessageVisitor visitor) throws ParseException {
    if (isCurrentlyRunningTest(name)) {
      myCurrentTestName = null;
      myCurrentTestResult = null;
    }
    String duration = myCurrentTestStart > 0 ? Long.toString(System.currentTimeMillis() - myCurrentTestStart) : null;
    switch (result) {
      case PASSED:
        break;
      case FAILED:
        String failedMessage = ServiceMessageBuilder.testFailed(name).addAttribute("message", "").toString();
        super.processServiceMessages(failedMessage, null, visitor);
        break;
      case SKIPPED:
        String skipMessage = ServiceMessageBuilder.testIgnored(name).addAttribute("message", "").toString();
        super.processServiceMessages(skipMessage, null, visitor);
        break;
    }
    String finishedMessage = ServiceMessageBuilder.testFinished(name).addAttribute("duration", duration).toString();
    super.processServiceMessages(finishedMessage, null, visitor);
  }

  @NotNull
  private static String testUrl(@NotNull String testName) {
    return GoTestLocator.PROTOCOL + "://" + testName;
  }
}
