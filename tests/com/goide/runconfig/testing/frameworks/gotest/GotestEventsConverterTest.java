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

package com.goide.runconfig.testing.frameworks.gotest;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.runconfig.testing.GoTestConsoleProperties;
import com.goide.runconfig.testing.frameworks.LoggingServiceMessageVisitor;
import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.SystemProperties;

import java.io.File;

public class GotestEventsConverterTest extends GoCodeInsightFixtureTestCase {

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
    return "testing/gotest";
  }

  private void doTest() throws Exception {
    Executor executor = new DefaultRunExecutor();
    GotestRunConfiguration runConfig = new GotestRunConfiguration(myFixture.getProject(), "", GotestRunConfigurationType.getInstance());
    GoTestConsoleProperties consoleProperties = new GoTestConsoleProperties(runConfig, executor);
    GotestEventsConverter converter = (GotestEventsConverter) consoleProperties.createTestEventsConverter("gotest", consoleProperties);

    String inputDataFilename = getTestName(true) + ".txt";
    LoggingServiceMessageVisitor serviceMessageVisitor = new LoggingServiceMessageVisitor();
    String lineSeparator = SystemProperties.getLineSeparator();
    for (String line : FileUtil.loadLines(new File(getTestDataPath() + "/" + inputDataFilename))) {
      converter.processServiceMessages(line + lineSeparator, ProcessOutputTypes.STDOUT, serviceMessageVisitor);
    }
    assertSameLinesWithFile(getTestDataPath() + "/" + getTestName(true) + "-expected.txt", serviceMessageVisitor.getLog());
  }
}
