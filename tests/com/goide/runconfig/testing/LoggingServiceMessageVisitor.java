/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import jetbrains.buildServer.messages.serviceMessages.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LoggingServiceMessageVisitor implements ServiceMessageVisitor {
  private static final String MY_INDENT = "  ";
  private final StringBuilder myLog = new StringBuilder();
  private String myIndent = "";

  @NotNull
  public String getLog() {
    return myLog.toString();
  }

  private void increaseIndent() {
    myIndent += MY_INDENT;
  }

  private void decreaseIndent() {
    myIndent = StringUtil.trimEnd(myIndent, MY_INDENT);
  }

  private void append(@NotNull MessageWithAttributes message) {
    myLog.append(myIndent).append(message.getClass().getSimpleName()).append('\n');
    increaseIndent();
    increaseIndent();
    for (Map.Entry<String, String> entry : ContainerUtil.newTreeMap(message.getAttributes()).entrySet()) {
      String key = entry.getKey();
      String value = "duration".equals(key) ? "42" : entry.getValue();
      myLog.append(myIndent).append("- ").append(key).append("=")
        .append(value.replace("\n", "\\n")).append('\n');
    }
    decreaseIndent();
    decreaseIndent();
  }

  @Override
  public void visitTestSuiteStarted(@NotNull TestSuiteStarted testSuiteStarted) {
    append(testSuiteStarted);
    increaseIndent();
  }

  @Override
  public void visitTestSuiteFinished(@NotNull TestSuiteFinished testSuiteFinished) {
    decreaseIndent();
    append(testSuiteFinished);
  }

  @Override
  public void visitTestStarted(@NotNull TestStarted testStarted) {
    append(testStarted);
    increaseIndent();
  }

  @Override
  public void visitTestFinished(@NotNull TestFinished testFinished) {
    decreaseIndent();
    append(testFinished);
  }

  @Override
  public void visitTestIgnored(@NotNull TestIgnored testIgnored) {
    append(testIgnored);
  }

  @Override
  public void visitTestStdOut(@NotNull TestStdOut testStdOut) {
    append(testStdOut);
  }

  @Override
  public void visitTestStdErr(@NotNull TestStdErr testStdErr) {
    append(testStdErr);
  }

  @Override
  public void visitTestFailed(@NotNull TestFailed testFailed) {
    append(testFailed);
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
