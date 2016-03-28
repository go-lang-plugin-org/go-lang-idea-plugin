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

package com.goide.runconfig.testing.frameworks.gocheck;

import com.goide.runconfig.testing.GoTestEventsConverterBase;
import com.goide.runconfig.testing.GoTestLocator;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.openapi.util.Pair.pair;

public class GocheckEventsConverter extends OutputToGeneralTestEventsConverter implements GoTestEventsConverterBase {
  private static final String FRAMEWORK_NAME = "gocheck";

  /*
   * Scope hierarchy looks like this:
   *
   * GLOBAL
   *   SUITE
   *     SUITE_SETUP
   *     TEST
   *       TEST_SETUP
   *       TEST_TEARDOWN
   *     SUITE_TEARDOWN
   */
  private enum Scope {
    GLOBAL,
    SUITE,
    SUITE_SETUP,
    SUITE_TEARDOWN,
    TEST,
    TEST_SETUP,
    TEST_TEARDOWN
  }

  private static final Pattern SUITE_START = Pattern.compile("=== RUN (.+)\\s*$");
  private static final Pattern SUITE_END = Pattern.compile("((PASS)|(FAIL))\\s*$");
  private static final Pattern TEST_START = Pattern.compile("(.*)START: [^:]+:\\d+: ([^\\s]+)\\s*$");
  private static final Pattern TEST_PASSED = Pattern.compile("(.*)PASS: [^:]+:\\d+: ([^\\s]+)\\t[^\\s]+\\s*$");
  private static final Pattern TEST_FAILED = Pattern.compile("(.*)FAIL: [^:]+:\\d+: ([^\\s]+)\\s*$");
  private static final Pattern TEST_PANICKED = Pattern.compile("(.*)PANIC: [^:]+:\\d+: ([^\\s]+)\\s*$");
  private static final Pattern TEST_MISSED = Pattern.compile("(.*)MISS: [^:]+:\\d+: ([^\\s]+)\\s*$");
  private static final Pattern TEST_SKIPPED = Pattern.compile("(.*)SKIP: [^:]+:\\d+: ([^\\s]+)( \\(.*\\))?\\s*$");
  private static final Pattern ERROR_LOCATION = Pattern.compile("(.*:\\d+):\\s*$");
  private static final Pattern ERROR_ACTUAL = Pattern.compile("\\.\\.\\. ((obtained)|(value)) (.*?)( \\+)?\\s*$");
  private static final Pattern ERROR_EXPECTED = Pattern.compile("\\.\\.\\. ((expected)|(regex)) (.*?)( \\+)?\\s*$");
  private static final Pattern ERROR_CONTINUATION = Pattern.compile("\\.\\.\\. {5}(.*?)( +\\+)?\\s*$");
  private static final Pattern PANIC_VALUE = Pattern.compile("(.*)\\.\\.\\. (Panic: .* \\(.*\\)\\s*)$");

  private Scope myScope = Scope.GLOBAL;
  private String mySuiteName;
  private String myTestName;
  private long myCurrentTestStart;
  private TestResult myFixtureFailure;
  private List<String> myStdOut;

  private enum Status {
    PASSED, FAILED, PANICKED, MISSED, SKIPPED
  }

  private static final class TestResult {
    private final Status myStatus;
    private final Map<String, String> myAttributes = ContainerUtil.newHashMap();

    TestResult(@NotNull Status status) {
      this(status, null);
    }

    TestResult(@NotNull Status status, @Nullable Map<String, String> attributes) {
      myStatus = status;
      if (attributes != null) myAttributes.putAll(attributes);
    }

    @NotNull
    public Status getStatus() {
      return myStatus;
    }

    public void addAttributesTo(@NotNull ServiceMessageBuilder serviceMessageBuilder) {
      for (Map.Entry<String, String> entry : myAttributes.entrySet()) {
        serviceMessageBuilder.addAttribute(entry.getKey(), entry.getValue());
      }
    }
  }

  public GocheckEventsConverter(@NotNull TestConsoleProperties consoleProperties) {
    super(FRAMEWORK_NAME, consoleProperties);
  }

  @Override
  public boolean processServiceMessages(@NotNull String text, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
    Matcher matcher;

    switch (myScope) {
      case GLOBAL:
        if (SUITE_START.matcher(text).matches()) {
          myScope = Scope.SUITE;
          return true;
        }
        break;

      case SUITE:
        if ((matcher = TEST_START.matcher(text)).matches()) {
          myStdOut = ContainerUtil.newArrayList();
          myTestName = matcher.group(2);
          processTestSectionStart(myTestName, outputType, visitor);
          if (myTestName.endsWith(".SetUpSuite")) {
            myScope = Scope.SUITE_SETUP;
            return true;
          }
          if (myTestName.endsWith(".TearDownSuite")) {
            myScope = Scope.SUITE_TEARDOWN;
            return true;
          }
          myScope = Scope.TEST;
          return processTestStarted(myTestName, outputType, visitor);
        }
        if (SUITE_END.matcher(text).matches()) {
          myScope = Scope.GLOBAL;
          if (mySuiteName != null) {
            String suiteFinishedMsg = ServiceMessageBuilder.testSuiteFinished(mySuiteName).toString();
            super.processServiceMessages(suiteFinishedMsg, outputType, visitor);
            processStdOut("SuiteTearDown", outputType, visitor);
          }
          return true;
        }
        break;

      case SUITE_SETUP:
        TestResult suiteSetUpResult = detectTestResult(text, true);
        if (suiteSetUpResult != null) {
          myScope = Scope.SUITE;
          if (suiteSetUpResult.getStatus() != Status.PASSED) {
            myFixtureFailure = suiteSetUpResult;
          }
          return true;
        }
        break;

      case SUITE_TEARDOWN:
        if (detectTestResult(text, false) != null) {
          myScope = Scope.SUITE;
          return true;
        }
        break;

      case TEST:
        if ((matcher = TEST_START.matcher(text)).matches()) {
          String stdOutLeftover = matcher.group(1);
          if (!StringUtil.isEmptyOrSpaces(stdOutLeftover)) {
            myStdOut.add(stdOutLeftover);
          }
          String testName = matcher.group(2);
          if (testName.endsWith(".SetUpTest")) {
            myScope = Scope.TEST_SETUP;
            return true;
          }
          if (testName.endsWith(".TearDownTest")) {
            myScope = Scope.TEST_TEARDOWN;
            return true;
          }
        }

        TestResult testResult = detectTestResult(text, true);
        if (testResult != null) {
          myScope = Scope.SUITE;
          if (StringUtil.notNullize(testResult.myAttributes.get("details")).contains("Fixture has panicked")
              || (testResult.getStatus() == Status.MISSED || testResult.getStatus() == Status.SKIPPED) && myFixtureFailure != null) {
            testResult = myFixtureFailure;
          }
          myFixtureFailure = null;
          processTestResult(testResult, outputType, visitor);
          return true;
        }
        break;

      case TEST_SETUP:
        TestResult testSetUpResult = detectTestResult(text, true);
        if (testSetUpResult != null) {
          myScope = Scope.TEST;
          if (testSetUpResult.getStatus() != Status.PASSED) {
            myFixtureFailure = testSetUpResult;
          }
          return true;
        }
        break;

      case TEST_TEARDOWN:
        boolean isSetUpFailed = myFixtureFailure != null;
        TestResult testTearDownResult = detectTestResult(text, !isSetUpFailed);
        if (testTearDownResult != null) {
          myScope = Scope.TEST;
          if (!isSetUpFailed && testTearDownResult.getStatus() != Status.PASSED) {
            myFixtureFailure = testTearDownResult;
          }
          return true;
        }
        break;
    }

    if (myStdOut != null) {
      myStdOut.add(text);
      return true;
    }

    return super.processServiceMessages(text, outputType, visitor);
  }

  @Nullable
  private TestResult detectTestResult(String text, boolean parseDetails) {
    Matcher matcher;
    if ((matcher = TEST_PASSED.matcher(text)).matches()) {
      myStdOut.add(StringUtil.notNullize(matcher.group(1)).trim());
      return new TestResult(Status.PASSED);
    }
    if ((matcher = TEST_MISSED.matcher(text)).matches()) {
      myStdOut.add(StringUtil.notNullize(matcher.group(1)).trim());
      return new TestResult(Status.MISSED);
    }
    if ((matcher = TEST_SKIPPED.matcher(text)).matches()) {
      myStdOut.add(StringUtil.notNullize(matcher.group(1)).trim());
      return new TestResult(Status.SKIPPED);
    }
    if ((matcher = TEST_FAILED.matcher(text)).matches()) {
      myStdOut.add(StringUtil.notNullize(matcher.group(1)).trim());
      if (parseDetails) {
        return new TestResult(Status.FAILED, parseFailureAttributes());
      }
      return new TestResult(Status.FAILED);
    }
    if ((matcher = TEST_PANICKED.matcher(text)).matches()) {
      myStdOut.add(StringUtil.notNullize(matcher.group(1)).trim());
      if (parseDetails) {
        return new TestResult(Status.PANICKED, parsePanickedAttributes());
      }
      return new TestResult(Status.FAILED);
    }
    return null;
  }

  private boolean processTestStarted(@NotNull String testName, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
    String testStartedMsg = ServiceMessageBuilder.testStarted(testName)
      .addAttribute("locationHint", testUrl(testName)).toString();
    return super.processServiceMessages(testStartedMsg, outputType, visitor);
  }

  private void processTestResult(@NotNull TestResult testResult, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
    processStdOut(myTestName, outputType, visitor);

    switch (testResult.getStatus()) {
      case PASSED:
        break;

      case MISSED:
      case SKIPPED:
        String testIgnoredStr = ServiceMessageBuilder.testIgnored(myTestName).toString();
        super.processServiceMessages(testIgnoredStr, outputType, visitor);
        break;
      
      case FAILED:
        ServiceMessageBuilder testError = ServiceMessageBuilder.testFailed(myTestName);
        testResult.addAttributesTo(testError);
        super.processServiceMessages(testError.toString(), outputType, visitor);
        break;

      case PANICKED:
        ServiceMessageBuilder testPanicked = ServiceMessageBuilder.testFailed(myTestName);
        testResult.addAttributesTo(testPanicked);
        super.processServiceMessages(testPanicked.toString(), outputType, visitor);
        break;

      default:
        throw new RuntimeException("Unexpected test result: " + testResult);
    }
    long duration = System.currentTimeMillis() - myCurrentTestStart;
    String testFinishedMsg = ServiceMessageBuilder.testFinished(myTestName).addAttribute("duration", Long.toString(duration)).toString();
    super.processServiceMessages(testFinishedMsg, outputType, visitor);
  }

  private void processStdOut(@NotNull String testName, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
    if (myStdOut == null) {
      return;
    }
    String allStdOut = StringUtil.join(myStdOut, "");
    if (!StringUtil.isEmptyOrSpaces(allStdOut)) {
      String testStdOutMsg = ServiceMessageBuilder.testStdOut(testName).addAttribute("out", allStdOut).toString();
      super.processServiceMessages(testStdOutMsg, outputType, visitor);
    }
    myStdOut = null;
  }

  private void processTestSectionStart(@NotNull String testName, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
    String suiteName = testName.substring(0, testName.indexOf("."));
    myTestName = testName;
    myCurrentTestStart = System.currentTimeMillis();
    if (!suiteName.equals(mySuiteName)) {
      if (mySuiteName != null) {
        String suiteFinishedMsg = ServiceMessageBuilder.testSuiteFinished(mySuiteName).toString();
        super.processServiceMessages(suiteFinishedMsg, outputType, visitor);
      }
      mySuiteName = suiteName;
      String suiteStartedMsg = ServiceMessageBuilder.testSuiteStarted(suiteName)
        .addAttribute("locationHint", suiteUrl(suiteName)).toString();
      super.processServiceMessages(suiteStartedMsg, outputType, visitor);
    }
  }

  /**
   * Parses assertion error report into a set of SystemMessage attributes.
   * <p/>
   * An assertion error report usually looks like this:
   * <pre>
   * all_fail_test.go:36:
   *     c.Assert("Foo", Equals, "Bar")
   * ... obtained string = "Foo"
   * ... expected string = "Bar"
   * </pre>
   * or this:
   * <pre>
   * all_fail_test.go:21:
   *     c.Assert("Foo", IsNil)
   * ... value string = "Foo"
   * </pre>
   * or this:
   * <pre>
   * all_fail_test.go:54:
   *     c.Assert(`multi
   *
   *     	          line
   *     	          string`,
   *         Equals,
   *         `Another
   *     multi
   *     	line
   *     		string`)
   * ... obtained string = "" +
   * ...     "multi\n" +
   * ...     "\n" +
   * ...     "\t          line\n" +
   * ...     "\t          string"
   * ... expected string = "" +
   * ...     "Another\n" +
   * ...     "multi\n" +
   * ...     "\tline\n" +
   * ...     "\t\tstring"
   * </pre>
   * There are other variation. Check out the respective unit test.
   *
   * @return a map of system message attributes.
   */
  @Nullable
  private Map<String, String> parseFailureAttributes() {
    if (myStdOut == null || myStdOut.isEmpty()) return null;

    int lineNumber = myStdOut.size() - 1;
    StringBuilder expectedMessage = new StringBuilder();
    StringBuilder actualMessage = new StringBuilder();
    StringBuilder errorMessage = new StringBuilder();
    String details = "";

    // Skip forward to the error description.
    while (lineNumber >= 0 && !StringUtil.startsWith(myStdOut.get(lineNumber), "...")) {
      lineNumber--;
    }

    lineNumber = collectErrorMessage(myStdOut, lineNumber, ERROR_CONTINUATION, ERROR_EXPECTED, expectedMessage);
    lineNumber = collectErrorMessage(myStdOut, lineNumber, ERROR_CONTINUATION, ERROR_ACTUAL, actualMessage);

    // Collect all lines of the error message and details
    while (lineNumber >= 0) {
      String line = myStdOut.get(lineNumber);
      Matcher matcher = ERROR_LOCATION.matcher(line);
      if (matcher.matches()) {
        details = matcher.group(1);
        break;
      }
      else {
        errorMessage.insert(0, line);
        lineNumber--;
      }
    }

    // Remove the assertion error info from the test StdOut.
    myStdOut = safeSublist(myStdOut, lineNumber);

    return ContainerUtil.newHashMap(pair("expected", expectedMessage.toString().trim()),
                                    pair("actual", actualMessage.toString().trim()),
                                    pair("type", "comparisonFailure"),
                                    pair("message", errorMessage.toString().trim()),
                                    pair("details", details));
  }

  private static int collectErrorMessage(List<String> lines, int currentLine, Pattern continuationPattern, Pattern messagePattern,
                                         StringBuilder result) {
    while (currentLine >= 0) {
      String line = lines.get(currentLine);

      Matcher continuationMatcher = continuationPattern.matcher(line);
      if (continuationMatcher.matches()) {
        result.insert(0, '\n').insert(0, continuationMatcher.group(1));
        currentLine--;
        continue;
      }

      Matcher messageMatcher = messagePattern.matcher(line);
      if (messageMatcher.matches()) {
        result.insert(0, '\n').insert(0, messageMatcher.group(4));
        currentLine--;
      }
      break;
    }
    return currentLine;
  }

  /**
   * Parses panic report into a set of SystemMessage attributes.
   * <p/>
   * A panic report usually looks like this:
   * <pre>
   * ... Panic: bar (PC=0x3B0A5)
   *
   * /usr/local/go/src/runtime/panic.go:387
   *   in gopanic
   * some_panic_test.go:31
   *   in SomePanicSuite.TestD
   * /usr/local/go/src/reflect/value.go:296
   *   in Value.Call
   * /usr/local/go/src/runtime/asm_amd64.s:2232
   *   in goexit
   * </pre>
   *
   * @return a map of system message attributes.
   */
  @Nullable
  private Map<String, String> parsePanickedAttributes() {
    if (myStdOut == null || myStdOut.isEmpty()) return null;

    int lineNumber = myStdOut.size() - 1;
    // Ignore trailing empty lines.
    while (lineNumber >= 0 && StringUtil.isEmptyOrSpaces(myStdOut.get(lineNumber))) {
      lineNumber--;
    }

    StringBuilder detailsMessage = new StringBuilder();
    // All lines up until an empty one comprise the stack trace.
    while (lineNumber >= 0 && !StringUtil.isEmptyOrSpaces(myStdOut.get(lineNumber))) {
      detailsMessage.insert(0, myStdOut.get(lineNumber));
      lineNumber--;
    }
    lineNumber--; // skip empty line

    // Then follows the panic description.
    String errorMessage = "";
    Matcher matcher;
    if (lineNumber >= 0 && (matcher = PANIC_VALUE.matcher(myStdOut.get(lineNumber))).matches()) {
      String stdoutLeftover = matcher.group(1);
      if (!StringUtil.isEmptyOrSpaces(stdoutLeftover)) {
        myStdOut.set(lineNumber, stdoutLeftover);
        lineNumber++;
      }
      errorMessage = matcher.group(2);
    }
    // Remove the panic info from the test StdOut.
    myStdOut = safeSublist(myStdOut, lineNumber);
    return ContainerUtil.newHashMap(pair("details", detailsMessage.toString()), pair("message", errorMessage));
  }

  @NotNull
  private static List<String> safeSublist(@NotNull List<String> list, int until) {
    if (0 < until && until <= list.size() - 1) {
      return list.subList(0, until);
    }
    return ContainerUtil.newArrayList();
  }

  @NotNull
  private static String suiteUrl(@NotNull String suiteName) {
    return GoTestLocator.SUITE_PROTOCOL + "://" + suiteName;
  }
  
  @NotNull
  private static String testUrl(@NotNull String testName) {
    return GoTestLocator.PROTOCOL + "://" + testName;
  }
}
