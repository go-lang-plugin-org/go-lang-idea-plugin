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

  private RunConfiguration myConfiguration;

  public GoTestConsoleProperties(@NotNull GoTestConfiguration configuration, @NotNull Executor executor) {
    super(new Storage.PropertiesComponentStorage("GoTestSupport.", PropertiesComponent.getInstance()), configuration.getProject(),
          executor);
    myConfiguration = configuration;
  }

  @Override
  public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName,
                                                                      @NotNull TestConsoleProperties consoleProperties) {
    return new GoOutputToGeneralTestEventsConverter(testFrameworkName, consoleProperties);
  }

  @Override
  public RunConfiguration getConfiguration() {
    return myConfiguration;
  }

  private static class GoOutputToGeneralTestEventsConverter extends OutputToGeneralTestEventsConverter {
    private static final Pattern RUN = Pattern.compile("^=== RUN (.+)");
    private static final Pattern PASSED = Pattern.compile("^--- PASS: ([^( ]+)");
    private static final Pattern FAILED = Pattern.compile("^--- FAIL: ([^( ]+)");
    private static final Pattern FINISHED = Pattern.compile("^(PASS)|(FAIL)$");

    private boolean myFailed = false;
    private StringBuilder myStdOut = new StringBuilder();
    private String myCurrentTest = "<test>";

    public GoOutputToGeneralTestEventsConverter(@NotNull String testFrameworkName,
                                                @NotNull TestConsoleProperties consoleProperties) {
      super(testFrameworkName, consoleProperties);
    }

    @Override
    protected boolean processServiceMessages(String text, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
      Matcher matcher;

      if ((matcher = RUN.matcher(text)).find()) {
        String testName = StringUtil.notNullize(matcher.group(1), "<test>");
        return super.processServiceMessages(ServiceMessageBuilder.testStarted(testName).toString(), outputType, visitor);
      }

      if ((matcher = PASSED.matcher(text)).find()) {
        String testName = StringUtil.notNullize(matcher.group(1), "<test>");
        return super.processServiceMessages(ServiceMessageBuilder.testFinished(testName).toString(), outputType, visitor);
      }

      if ((matcher = FAILED.matcher(text)).find()) {
        myFailed = true;
        myCurrentTest = StringUtil.notNullize(matcher.group(1), "<test>");
        return true;
      }

      if (myFailed) {
        if (!StringUtil.isEmptyOrSpaces(text) && !FINISHED.matcher(text).find()) {
          myStdOut.append(text);
        }
        else {
          String failedMessage = ServiceMessageBuilder.testFailed(myCurrentTest).addAttribute("message", myStdOut.toString()).toString();
          myFailed = false;
          myStdOut = new StringBuilder();
          return super.processServiceMessages(failedMessage, outputType, visitor)
                 && super.processServiceMessages(ServiceMessageBuilder.testFinished(myCurrentTest).toString(), outputType, visitor);
        }
      }

      return true;
    }

    private String failedMessage() {
      return ServiceMessageBuilder.testFailed(myCurrentTest).addAttribute("message", myStdOut.toString()).toString();
    }
  }
}
