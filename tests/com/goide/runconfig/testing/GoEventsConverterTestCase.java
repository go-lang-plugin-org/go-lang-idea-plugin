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

package com.goide.runconfig.testing;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public abstract class GoEventsConverterTestCase extends GoCodeInsightFixtureTestCase {
  protected void doTest() {
    Executor executor = new DefaultRunExecutor();
    GoTestRunConfiguration runConfig = new GoTestRunConfiguration(myFixture.getProject(), "", GoTestRunConfigurationType.getInstance());
    runConfig.setTestFramework(getTestFramework());
    GoTestConsoleProperties consoleProperties = new GoTestConsoleProperties(runConfig, executor);
    GoTestEventsConverterBase converter =
      (GoTestEventsConverterBase)consoleProperties.createTestEventsConverter("gotest", consoleProperties);

    LoggingServiceMessageVisitor serviceMessageVisitor = new LoggingServiceMessageVisitor();
    try {
      for (String line : FileUtil.loadLines(new File(getTestDataPath(), getTestName(true) + ".txt"), CharsetToolkit.UTF8)) {
        converter.processServiceMessages(line + "\n", ProcessOutputTypes.STDOUT, serviceMessageVisitor);
      }
    }
    catch (IOException | ParseException e) {
      throw new RuntimeException(e);
    }
    ((OutputToGeneralTestEventsConverter)converter).flushBufferBeforeTerminating();
    Disposer.dispose((OutputToGeneralTestEventsConverter)converter);
    assertSameLinesWithFile(getTestDataPath() + "/" + getTestName(true) + "-expected.txt", serviceMessageVisitor.getLog());
  }

  @NotNull
  protected abstract GoTestFramework getTestFramework();
}

