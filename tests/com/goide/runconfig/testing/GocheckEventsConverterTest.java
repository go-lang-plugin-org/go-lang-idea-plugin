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

package com.goide.runconfig.testing;

import com.goide.runconfig.testing.frameworks.gocheck.GocheckFramework;
import org.jetbrains.annotations.NotNull;

public class GocheckEventsConverterTest extends GoEventsConverterTestCase {

  public void testPass() throws Exception { doTest(); }
  public void testAssertions() throws Exception { doTest(); }
  public void testAssertionsInvalidFormat() throws Exception { doTest(); }
  public void testPanic() throws Exception { doTest(); }
  public void testPanicInvalidFormat() throws Exception { doTest(); }
  public void testFixtureStdOut() throws Exception { doTest(); }
  public void testSuiteSetUpError() throws Exception { doTest(); }
  public void testSuiteTearDownError() throws Exception { doTest(); }
  public void testTestSetUpError() throws Exception { doTest(); }
  public void testTestTearDownError() throws Exception { doTest(); }
  public void testTestErrorWithFixtures() throws Exception { doTest(); }
  public void testTestAndTestTearDownError() throws Exception { doTest(); }
  public void testTestBothFixturesError() throws Exception { doTest(); }
  public void testSkippingTests() throws Exception { doTest(); }

  @Override
  protected String getBasePath() {
    return "testing/gocheck";
  }
  
  @NotNull
  @Override
  protected GoTestFramework getTestFramework() {
    return GocheckFramework.INSTANCE;
  }
}
