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

package com.goide.runconfig.testing.frameworks.gocheck;

import com.goide.runconfig.testing.GoTestLocationProvider;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GocheckEventsConverter extends OutputToGeneralTestEventsConverter {
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

  private static final Pattern SUITE_START = Pattern.compile("=== RUN (.+)\\n");
  private static final Pattern SUITE_END = Pattern.compile("((PASS)|(FAIL))\\n");
  private static final Pattern TEST_START = Pattern.compile("(.*)START: [^:]+:\\d+: ([^\\s]+)\\n");
  private static final Pattern TEST_PASSED = Pattern.compile("(.*)PASS: [^:]+:\\d+: ([^\\s]+)\\t[^\\s]+\\n");
  private static final Pattern TEST_FAILED = Pattern.compile("(.*)FAIL: [^:]+:\\d+: ([^\\s]+)\\n");
  private static final Pattern TEST_PANICKED = Pattern.compile("(.*)PANIC: [^:]+:\\d+: ([^\\s]+)\\n");
  private static final Pattern TEST_MISSED = Pattern.compile("(.*)MISS: [^:]+:\\d+: ([^\\s]+)\\n");
  private static final Pattern ERROR_LOCATION = Pattern.compile("(.*:\\d+):\\n");
  private static final Pattern ERROR_ACTUAL = Pattern.compile("\\.\\.\\. ((obtained)|(value)) (.*?)( \\+)?\\n");
  private static final Pattern ERROR_EXPECTED = Pattern.compile("\\.\\.\\. ((expected)|(regex)) (.*?)( \\+)?\\n");
  private static final Pattern ERROR_CONTINUATION = Pattern.compile("\\.\\.\\. {5}(.*?)( +\\+)?\\n");
  private static final Pattern PANIC_VALUE = Pattern.compile("(.*)\\.\\.\\. (Panic: .* \\(.*\\)\\n)");

  private Scope myScope = Scope.GLOBAL;
  private String mySuiteName;
  private String myTestName;
  private TestResult myFixtureFailure;
  private List<String> myStdOut;

  private enum Status {
    PASSED, FAILED, PANICKED, MISSED
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
  protected boolean processServiceMessages(@NotNull String text, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
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
          if ((StringUtil.notNullize(testResult.myAttributes.get("details")).contains("Fixture has panicked"))
              || (testResult.getStatus() == Status.MISSED && myFixtureFailure != null)) {
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
        boolean isSetUpFailed = (myFixtureFailure != null);
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
      myStdOut.add(StringUtil.notNullize(matcher.group(1), "").trim());
      return new TestResult(Status.PASSED);
    }
    if ((matcher = TEST_MISSED.matcher(text)).matches()) {
      myStdOut.add(StringUtil.notNullize(matcher.group(1), "").trim());
      return new TestResult(Status.MISSED);
    }
    if ((matcher = TEST_FAILED.matcher(text)).matches()) {
      myStdOut.add(StringUtil.notNullize(matcher.group(1), "").trim());
      if (parseDetails) {
        return new TestResult(Status.FAILED, parseFailureAttributes());
      }
      return new TestResult(Status.FAILED);
    }
    if ((matcher = TEST_PANICKED.matcher(text)).matches()) {
      myStdOut.add(StringUtil.notNullize(matcher.group(1), "").trim());
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
        throw new RuntimeException("Unexpected test result: " + testResult.toString());
    }
    String testFinishedMsg = ServiceMessageBuilder.testFinished(myTestName).toString();
    super.processServiceMessages(testFinishedMsg, outputType, visitor);
  }

  private void processStdOut(@NotNull String testName, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
    if (myStdOut == null) {
      return;
    }
    String allStdOut = StringUtil.join(myStdOut, "");
    if (!StringUtil.isEmptyOrSpaces(allStdOut)) {
      String testStdOutMsg = ServiceMessageBuilder.testStdOut(testName)
        .addAttribute("out", allStdOut).toString();
      super.processServiceMessages(testStdOutMsg, outputType, visitor);
    }
    myStdOut = null;
  }

  private void processTestSectionStart(@NotNull String testName, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
    String suiteName = testName.substring(0, testName.indexOf("."));
    myTestName = testName;
    if (!suiteName.equals(mySuiteName)) {
      if (mySuiteName != null) {
        String suiteFinishedMsg = ServiceMessageBuilder.testSuiteFinished(mySuiteName).toString();
        super.processServiceMessages(suiteFinishedMsg, outputType, visitor);
      }
      mySuiteName = suiteName;
      String suiteStartedMsg = ServiceMessageBuilder.testSuiteStarted(suiteName).toString();
      super.processServiceMessages(suiteStartedMsg, outputType, visitor);
    }
  }

  /**
   * Parses assertion error report into a set of SystemMessage attributes.
   *
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
  private Map<String, String> parseFailureAttributes() {
    assert myStdOut != null: "expected to be called in a context where myStdOut is not null";

    int i = myStdOut.size() - 1;
    Map<String, String> attributes = ContainerUtil.newHashMap();
    attributes.put("type", "comparisonFailure");

    // Traverse the test StdOut in bottom-up direction and retrieve the
    // assertion error parameters.
    try {
      List<String> buffer = ContainerUtil.newArrayList();
      Matcher matcher;

      // Skip forward to the error description.
      while (!StringUtil.startsWith(myStdOut.get(i), "...")) {
        --i;
      }

      // Collected all lines of the expected value.
      String expected = "";
      while ((matcher = ERROR_CONTINUATION.matcher(myStdOut.get(i))).matches()) {
        --i;
        buffer.add(matcher.group(1));
      }
      if ((matcher = ERROR_EXPECTED.matcher(myStdOut.get(i))).matches()) {
        --i;
        buffer.add(matcher.group(4));
        Collections.reverse(buffer);
        expected = StringUtil.join(buffer, "\n");
        buffer.clear();
      }
      attributes.put("expected", expected);

      // Collect all lines of the actual value
      String actual = "";
      while ((matcher = ERROR_CONTINUATION.matcher(myStdOut.get(i))).matches()) {
        --i;
        buffer.add(matcher.group(1));
      }
      if ((matcher = ERROR_ACTUAL.matcher(myStdOut.get(i))).matches()) {
        --i;
        buffer.add(matcher.group(4));
        Collections.reverse(buffer);
        actual = StringUtil.join(buffer, "\n");
        buffer.clear();
      }
      attributes.put("actual", actual);

      // Collect all lines of the error message and details
      while (!(matcher = ERROR_LOCATION.matcher(myStdOut.get(i))).matches()) {
        buffer.add(myStdOut.get(i));
        --i;
      }
      Collections.reverse(buffer);
      attributes.put("message", StringUtil.join(buffer, "").trim());
      attributes.put("details", matcher.group(1));
    }
    catch (IndexOutOfBoundsException e) {
      // The assertion error description is not formatted as expected,
      // so it will not be made into a ServiceMessage but will stay in
      // the test StdOut.
      return Collections.emptyMap();
    }
    // Remove the assertion error info from the test StdOut.
    myStdOut = myStdOut.subList(0, i);

    return attributes;
  }

  /**
   * Parses panic report into a set of SystemMessage attributes.
   *
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
  private Map<String, String> parsePanickedAttributes() {
    assert myStdOut != null: "expected to be called from a context where myStdOut is not null";

    int i = myStdOut.size() - 1;
    Map<String, String> attributes = ContainerUtil.newHashMap();
    // Traverse the test StdOut in bottom-up direction and retrieve the
    // panic parameters.
    try {
      List<String> buffer = ContainerUtil.newArrayList();
      Matcher matcher;

      // Ignore trailing empty lines.
      while (StringUtil.isEmptyOrSpaces(myStdOut.get(i))) {
        --i;
      }

      // All lines up until an empty one comprise the stack trace.
      while (!StringUtil.isEmptyOrSpaces(myStdOut.get(i))) {
        buffer.add(myStdOut.get(i--));
      }
      Collections.reverse(buffer);
      attributes.put("details", StringUtil.join(buffer, ""));

      // Skip the empty line.
      --i;

      // Then follows the panic description.
      String errorMessage = "";
      if ((matcher = PANIC_VALUE.matcher(myStdOut.get(i))).matches()) {
        String stdoutLeftover = matcher.group(1);
        if (!StringUtil.isEmptyOrSpaces(stdoutLeftover)) {
          myStdOut.set(i, stdoutLeftover);
          ++i;
        }
        errorMessage = matcher.group(2);
      }
      attributes.put("message", errorMessage);
    }
    catch (IndexOutOfBoundsException e) {
      // The panic description is not formatted as expected, so it will
      // not be made into a ServiceMessage but will stay in the test StdOut.
      return Collections.emptyMap();
    }

    // Remove the panic info from the test StdOut.
    myStdOut = myStdOut.subList(0, i);

    return attributes;
  }

  @NotNull
  private static String testUrl(@NotNull String testName) {
    return GoTestLocationProvider.PROTOCOL + "://" + testName;
  }
}
