/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

package com.goide.runconfig.testing;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing;
import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.config.Storage;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageVisitor;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoTestConsoleProperties extends TestConsoleProperties implements SMCustomMessagesParsing {

  private final RunConfiguration myConfiguration;

  public GoTestConsoleProperties(@NotNull GoTestRunConfiguration configuration, @NotNull Executor executor) {
    super(new Storage.PropertiesComponentStorage("GoTestSupport.", PropertiesComponent.getInstance()), configuration.getProject(),
          executor);
    myConfiguration = configuration;
  }

  @NotNull
  @Override
  public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName,
                                                                      @NotNull TestConsoleProperties consoleProperties) {
    return new GoOutputToGeneralTestEventsConverter(testFrameworkName, consoleProperties);
  }

  @Override
  public RunConfiguration getConfiguration() {
    return myConfiguration;
  }

  public static class GoOutputToGeneralTestEventsConverter extends OutputToGeneralTestEventsConverter {
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

    public GoOutputToGeneralTestEventsConverter(@NotNull String testFrameworkName,
                                                @NotNull TestConsoleProperties consoleProperties) {
      super(testFrameworkName, consoleProperties);
    }

    @Override
    protected boolean processServiceMessages(@NotNull String text, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
      Matcher matcher;

      if ((matcher = RUN.matcher(text)).find()) {
        myOutputAppeared = false;
        String testName = StringUtil.notNullize(matcher.group(1), "<test>");
        ServiceMessageBuilder testStarted = ServiceMessageBuilder.testStarted(testName).addAttribute("locationHint", testUrl(testName));
        return processNotFinishedMessage(testStarted.toString(), outputType, visitor);
      }

      if ((matcher = PASSED.matcher(text)).find()) {
        String testName = StringUtil.notNullize(matcher.group(1), "<test>");
        return handleFinishTest(text, matcher, testName, outputType, visitor) 
               && processNotFinishedMessage(ServiceMessageBuilder.testFinished(testName).toString(), outputType, visitor);
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
             && super.processServiceMessages(ServiceMessageBuilder.testFinished(myCurrentTest).toString(), outputType, visitor);
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
}
