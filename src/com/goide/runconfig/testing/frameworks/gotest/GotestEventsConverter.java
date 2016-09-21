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

import com.goide.GoConstants;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.openapi.util.Key;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageVisitor;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GotestEventsConverter extends GoTestEventsConverterBaseImpl {
  private static final Pattern RUN = Pattern.compile("^=== RUN\\s+(" + GoConstants.TEST_NAME_REGEX + ")");
  private static final Pattern PASSED = Pattern.compile("--- PASS:\\s+(" + GoConstants.TEST_NAME_REGEX + ")");
  private static final Pattern SKIP = Pattern.compile("--- SKIP:\\s+(" + GoConstants.TEST_NAME_REGEX + ")");
  private static final Pattern FAILED = Pattern.compile("--- FAIL:\\s+(" + GoConstants.TEST_NAME_REGEX + ")");
  private static final Pattern FINISHED = Pattern.compile("^(PASS)|(FAIL)$");

  public GotestEventsConverter(@NotNull TestConsoleProperties consoleProperties) {
    super(GotestFramework.NAME, consoleProperties);
  }

  @Override
  protected int processLine(@NotNull String line, int start, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
    Matcher matcher;
    if ((matcher = RUN.matcher(line)).find(start)) {
      startTest(matcher.group(1), visitor);
      return line.length();
    }
    if ((matcher = SKIP.matcher(line)).find(start)) {
      processOutput(line.substring(start, matcher.start()), outputType, visitor);
      finishTest(matcher.group(1), TestResult.SKIPPED, visitor);
      return line.length();
    }
    if ((matcher = FAILED.matcher(line)).find(start)) {
      processOutput(line.substring(start, matcher.start()), outputType, visitor);
      finishTest(matcher.group(1), TestResult.FAILED, visitor);
      return line.length();
    }
    if ((matcher = PASSED.matcher(line)).find(start)) {
      processOutput(line.substring(start, matcher.start()), outputType, visitor);
      finishTest(matcher.group(1), TestResult.PASSED, visitor);
      return line.length();
    }
    if (FINISHED.matcher(line).find(start)) {
      finishDelayedTest(visitor);
      return line.length();
    }
    return start;
  }
}
