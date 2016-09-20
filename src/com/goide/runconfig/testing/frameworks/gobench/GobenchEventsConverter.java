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
import com.goide.runconfig.testing.frameworks.gotest.GoTestEventsConverterBaseImpl;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GobenchEventsConverter extends GoTestEventsConverterBaseImpl {
  private static final Pattern RUN = Pattern.compile("^(Benchmark(" + GoConstants.TEST_NAME_REGEX + ")?)");
  private static final Pattern FAIL = Pattern.compile("^--- FAIL: (Benchmark(" + GoConstants.TEST_NAME_REGEX + ")?).*");

  public GobenchEventsConverter(@NotNull TestConsoleProperties properties) {
    super(GobenchFramework.NAME, properties);
  }

  @Override
  protected int processLine(@NotNull String line, int start, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
    Matcher matcher;
    if ((matcher = RUN.matcher(line)).find(start)) {
      startTest(matcher.group(1), visitor);
      int newStartOffset = findFirstNonWSIndex(line, matcher.end(1));
      return newStartOffset != -1 ? newStartOffset : line.length();
    }
    if ((matcher = FAIL.matcher(line.substring(start))).find()) {
      finishTest(matcher.group(1), TestResult.FAILED, visitor);
      int newStartOffset = findFirstNonWSIndex(line, start + matcher.end(1));
      return newStartOffset != -1 ? newStartOffset : line.length();
    }
    return start;
  }

  @Override
  protected void startTest(@NotNull String testName, @Nullable ServiceMessageVisitor visitor) throws ParseException {
    String currentTestName = getCurrentTestName();
    if (currentTestName != null && !currentTestName.equals(testName)) {
      // previous test didn't fail, finish it as passed
      finishTest(currentTestName, TestResult.PASSED, visitor);
    }
    super.startTest(testName, visitor);
  }

  private static int findFirstNonWSIndex(@NotNull String text, int startOffset) {
    int whitespaceIndex = StringUtil.indexOfAny(text, " \t", startOffset, text.length());
    if (whitespaceIndex != -1) {
      int newStartOffset = whitespaceIndex;
      while (newStartOffset < text.length() && Character.isWhitespace(text.charAt(newStartOffset))) newStartOffset++;
      return newStartOffset;
    }
    return -1;
  }
}