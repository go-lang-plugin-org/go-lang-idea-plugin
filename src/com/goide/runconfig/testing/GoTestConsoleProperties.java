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
    private static final Pattern PASSED = Pattern.compile("^--- PASS: ([^( ]+)");
    private static final Pattern FAILED = Pattern.compile("^--- FAIL: ([^( ]+)");
    private static final Pattern FINISHED = Pattern.compile("^(PASS)|(FAIL)$");

    private boolean myFailed = false;
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
        String testName = StringUtil.notNullize(matcher.group(1), "<test>");
        ServiceMessageBuilder testStarted = ServiceMessageBuilder.testStarted(testName).addAttribute("locationHint", "gotest://" + testName);
        return processNotFinishedMessage(testStarted.toString(), outputType, visitor);
      }

      if ((matcher = PASSED.matcher(text)).find()) {
        String testName = StringUtil.notNullize(matcher.group(1), "<test>");
        return processNotFinishedMessage(ServiceMessageBuilder.testFinished(testName).toString(), outputType, visitor);
      }

      if ((matcher = FAILED.matcher(text)).find()) {
        myFailed = true;
        myCurrentTest = StringUtil.notNullize(matcher.group(1), "<test>");
        return true;
      }

      if (myFailed) {
        if (!StringUtil.isEmptyOrSpaces(text) && !FINISHED.matcher(text).find()) {
          myStdOut.append(text);
          return true;
        }
        else {
          return processFailedMessage(outputType, visitor);
        }
      }

      return super.processServiceMessages(text, outputType, visitor);
    }

    private boolean processNotFinishedMessage(String message, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
      if (myFailed) {
        processFailedMessage(outputType, visitor);
      }
      return super.processServiceMessages(message, outputType, visitor);
    }

    private boolean processFailedMessage(Key outputType, ServiceMessageVisitor visitor) throws ParseException {
      String failedMessage = ServiceMessageBuilder.testFailed(myCurrentTest).addAttribute("message", myStdOut.toString()).toString();
      myFailed = false;
      myStdOut = new StringBuilder();
      return super.processServiceMessages(failedMessage, outputType, visitor)
             && super.processServiceMessages(ServiceMessageBuilder.testFinished(myCurrentTest).toString(), outputType, visitor);
    }
  }
}
