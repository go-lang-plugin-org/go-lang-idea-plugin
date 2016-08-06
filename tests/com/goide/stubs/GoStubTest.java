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

package com.goide.stubs;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.psi.GoFile;
import com.intellij.testFramework.ParsingTestCase;

import java.io.IOException;

public class GoStubTest extends GoCodeInsightFixtureTestCase {
  public void testStub() throws IOException {
    String text = "package main; func main() { type A struct { a int } }";
    GoFile file = (GoFile)myFixture.addFileToProject("m.go", text);
    String s = buildStubTreeText(getProject(), file.getVirtualFile(), text);
    ParsingTestCase.doCheckResult(getTestDataPath(), getTestName(true) + ".txt", s);
  }

  @Override
  protected String getBasePath() {
    return "stubs";
  }
}
