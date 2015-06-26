/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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
import com.goide.runconfig.testing.GoTestLocationProvider;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageVisitor;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GotestEventsConverter extends OutputToGeneralTestEventsConverter implements GoTestEventsConverterBase {
  private static final String FRAMEWORK_NAME = "gotest";

  private static final Pattern RUN = Pattern.compile("^=== RUN (.+)");
  private static final Pattern PASSED = Pattern.compile("--- PASS: ([^( ]+)");
  private static final Pattern SKIP = Pattern.compile("--- SKIP: ([^( ]+)");
  private static final Pattern FAILED = Pattern.compile("--- FAIL: ([^( ]+)");
  private static final Pattern FINISHED = Pattern.compile("^(PASS)|(FAIL)$");

  private boolean myFailed = false;
  private boolean mySkipped = false;
  private boolean myOutputAppeared = false;
  @NotNull private StringBuilder myStdOut = new StringBuilder();
  @NotNull private String myCurrentTest = "<test>";
  private long myCurrentTestStart;

  public GotestEventsConverter(@NotNull TestConsoleProperties consoleProperties) {
    super(FRAMEWORK_NAME, consoleProperties);
  }

  @Override
  public boolean processServiceMessages(@NotNull String text, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
    Matcher matcher;

    if ((matcher = RUN.matcher(text)).find()) {
      myOutputAppeared = false;
      String testName = StringUtil.notNullize(matcher.group(1), "<test>");
      ServiceMessageBuilder testStarted = ServiceMessageBuilder.testStarted(testName).addAttribute("locationHint", testUrl(testName));
      boolean result = processNotFinishedMessage(testStarted.toString(), outputType, visitor);
      myCurrentTestStart = System.currentTimeMillis();
      return result;
    }

    if ((matcher = PASSED.matcher(text)).find()) {
      String testName = StringUtil.notNullize(matcher.group(1), "<test>");
      return handleFinishTest(text, matcher, testName, outputType, visitor)
             && processNotFinishedMessage(testFinishedMessage(testName), outputType, visitor);
    }

    if ((matcher = SKIP.matcher(text)).find()) {
      mySkipped = true;
      myCurrentTest = StringUtil.notNullize(matcher.group(1), "<test>");
      handleFinishTest(text, matcher, myCurrentTest, outputType, visitor);
      return true;
    }

    if ((matcher = FAILED.matcher(text)).find()) {
      myFailed = true;
      myCurrentTest = StringUtil.notNullize(matcher.group(1), "<test>");
      handleFinishTest(text, matcher, myCurrentTest, outputType, visitor);
      return true;
    }

    if (myFailed || mySkipped) {
      if (!StringUtil.isEmptyOrSpaces(text) && !FINISHED.matcher(text).find()) {
        myStdOut.append(text);
        return true;
      }
      else {
        return processFailedMessage(outputType, visitor);
      }
    }

    myOutputAppeared = true;
    return super.processServiceMessages(text, outputType, visitor);
  }

  @NotNull
  private String testFinishedMessage(String testName) {
    long duration = System.currentTimeMillis() - myCurrentTestStart;
    return ServiceMessageBuilder.testFinished(testName).addAttribute("duration", Long.toString(duration)).toString();
  }

  @NotNull
  private static String testUrl(@NotNull String testName) {
    return GoTestLocationProvider.PROTOCOL + "://" + testName;
  }

  private boolean processNotFinishedMessage(String message, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
    if (myFailed || mySkipped) {
      processFailedMessage(outputType, visitor);
    }
    return super.processServiceMessages(message, outputType, visitor);
  }

  private boolean processFailedMessage(Key outputType, ServiceMessageVisitor visitor) throws ParseException {
    ServiceMessageBuilder builder = myFailed
                                    ? ServiceMessageBuilder.testFailed(myCurrentTest)
                                    : ServiceMessageBuilder.testIgnored(myCurrentTest);
    String message = builder.addAttribute("message", myStdOut.toString().trim() + "\n").toString();

    myFailed = false;
    mySkipped = false;
    myStdOut = new StringBuilder();
    return super.processServiceMessages(message, outputType, visitor)
           && super.processServiceMessages(testFinishedMessage(myCurrentTest), outputType, visitor);
  }

  private boolean handleFinishTest(String text, Matcher matcher, String testName, Key outputType, ServiceMessageVisitor visitor)
    throws ParseException {
    addNewLineIfNeeded(testName, outputType, visitor);
    if (matcher.start() > 0) {
      myOutputAppeared = true;
      String out = text.substring(0, matcher.start()) + (!myFailed && !mySkipped ? "\n" : "");
      ServiceMessageBuilder message = ServiceMessageBuilder.testStdOut(testName).addAttribute("out", out);
      super.processServiceMessages(message.toString(), outputType, visitor);
    }
    return true;
  }

  private boolean addNewLineIfNeeded(@NotNull String testName, @NotNull Key outputType, @NotNull ServiceMessageVisitor visitor)
    throws ParseException {
    ServiceMessageBuilder message = ServiceMessageBuilder.testStdOut(testName).addAttribute("out", "\n");
    return !myOutputAppeared || super.processServiceMessages(message.toString(), outputType, visitor);
  }
}
