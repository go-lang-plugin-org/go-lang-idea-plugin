package com.goide.runconfig.testing;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.SystemProperties;
import jetbrains.buildServer.messages.serviceMessages.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class GoUnitTestEventsConverterTest extends GoCodeInsightFixtureTestCase {

  public void testSingleTestFailed() throws Exception {
    doTest();
  }

  public void testSingleTestOk() throws Exception {
    doTest();
  }

  public void testMultipleTestsFailed() throws Exception {
    doTest();
  }

  public void testMultipleTestsOk() throws Exception {
    doTest();
  }
  
  public void testSkipTest() throws Exception {
    doTest();
  }
  
  public void testStdOut() throws Exception {
    doTest();
  }
  
  public void testOneLineEvents() throws Exception {
    doTest();
  }

  @Override
  protected String getBasePath() {
    return "testing";
  }

  private void doTest() throws Exception {
    Executor executor = new DefaultRunExecutor();
    GoTestRunConfiguration runConfig = new GoTestRunConfiguration(myFixture.getProject(), "", GoTestRunConfigurationType.getInstance());
    GoTestConsoleProperties consoleProperties = new GoTestConsoleProperties(runConfig, executor);
    GoTestConsoleProperties.GoOutputToGeneralTestEventsConverter converter =
      new GoTestConsoleProperties.GoOutputToGeneralTestEventsConverter("", consoleProperties);

    String inputDataFilename = getTestName(true) + ".txt";
    LoggingServiceMessageVisitor serviceMessageVisitor = new LoggingServiceMessageVisitor();
    String lineSeparator = SystemProperties.getLineSeparator();
    for (String line : FileUtil.loadLines(new File(getTestDataPath() + "/" + inputDataFilename))) {
      converter.processServiceMessages(line + lineSeparator, ProcessOutputTypes.STDOUT, serviceMessageVisitor);
    }
    assertSameLinesWithFile(getTestDataPath() + "/" + getTestName(true) + "-expected.txt", serviceMessageVisitor.getLog());
  }

  private static class LoggingServiceMessageVisitor implements ServiceMessageVisitor {
    private static final String MY_INDENT = "  ";
    private final StringBuilder myLog = new StringBuilder();
    private String myIndent = "";

    public String getLog() {
      return myLog.toString();
    }

    private void increaseIndent() {
      myIndent += MY_INDENT;
    }

    private void decreaseIndent() {
      myIndent = StringUtil.trimEnd(myIndent, MY_INDENT);
    }

    private void append(String event) {
      append(event, "");
    }

    private void append(String event, String eventDetails) {
      String delimiter = StringUtil.isEmpty(eventDetails) || Character.isWhitespace(eventDetails.charAt(0)) ? "" : ": ";
      myLog.append(myIndent).append(event).append(delimiter).append(eventDetails).append('\n');
    }

    @Override
    public void visitTestSuiteStarted(@NotNull TestSuiteStarted testSuiteStarted) {
      append("started_suite", testSuiteStarted.getSuiteName());
      increaseIndent();
    }

    @Override
    public void visitTestSuiteFinished(@NotNull TestSuiteFinished testSuiteFinished) {
      decreaseIndent();
      append("finished_suite", testSuiteFinished.getSuiteName());
    }

    @Override
    public void visitTestStarted(@NotNull TestStarted testStarted) {
      append("started_test", testStarted.getTestName());
      increaseIndent();
    }

    @Override
    public void visitTestFinished(@NotNull TestFinished testFinished) {
      decreaseIndent();
      append("finished_test", testFinished.getTestName());
    }

    @Override
    public void visitTestIgnored(@NotNull TestIgnored testIgnored) {
      append("ignored_test", testIgnored.getTestName());
      increaseIndent();
      append(testIgnored.getIgnoreComment().trim());
      decreaseIndent();
    }

    @Override
    public void visitTestStdOut(@NotNull TestStdOut testStdOut) {
      append("test_output", testStdOut.getStdOut());
    }

    @Override
    public void visitTestStdErr(@NotNull TestStdErr testStdErr) {
      append("failed_test_message", testStdErr.getStdErr());
    }

    @Override
    public void visitTestFailed(@NotNull TestFailed testFailed) {
      append("failed_test", testFailed.getTestName());
      increaseIndent();
      append(testFailed.getFailureMessage().trim());
      decreaseIndent();
    }

    @Override
    public void visitPublishArtifacts(@NotNull PublishArtifacts artifacts) {
    }

    @Override
    public void visitProgressMessage(@NotNull ProgressMessage message) {
    }

    @Override
    public void visitProgressStart(@NotNull ProgressStart start) {
    }

    @Override
    public void visitProgressFinish(@NotNull ProgressFinish finish) {
    }

    @Override
    public void visitBuildStatus(@NotNull BuildStatus status) {
    }

    @Override
    public void visitBuildNumber(@NotNull BuildNumber number) {
    }

    @Override
    public void visitBuildStatisticValue(@NotNull BuildStatisticValue value) {
    }

    @Override
    public void visitMessageWithStatus(@NotNull Message message) {
    }

    @Override
    public void visitBlockOpened(@NotNull BlockOpened opened) {
    }

    @Override
    public void visitBlockClosed(@NotNull BlockClosed closed) {
    }

    @Override
    public void visitCompilationStarted(@NotNull CompilationStarted started) {
    }

    @Override
    public void visitCompilationFinished(@NotNull CompilationFinished finished) {
    }

    @Override
    public void visitServiceMessage(@NotNull ServiceMessage message) {
    }
  }
}
