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

package com.goide.completion;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.project.GoExcludedPathsSettings;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.util.ArrayUtil;

public class GoExcludePackagesTest extends GoCodeInsightFixtureTestCase {
  GoExcludedPathsSettings mySettings;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
    mySettings = GoExcludedPathsSettings.getInstance(getProject());
  }

  @Override
  protected void tearDown() throws Exception {
    GoExcludedPathsSettings.getInstance(getProject()).setExcludedPackages(ArrayUtil.EMPTY_STRING_ARRAY);
    super.tearDown();
  }

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }

  private void doTestExcluded(String initial, String after, String... excludedPaths) {
    mySettings.setExcludedPackages(excludedPaths);

    myFixture.configureByText("a.go", initial);
    myFixture.completeBasic();
    myFixture.checkResult(after);
  }

  public void testExcludedPathCompletion() {
    String initial = "package a; func b() {\n fmt.Printl<caret> \n}";
    doTestExcluded(initial, initial, "fmt");
  }

  public void testExcludedPathSameBeginning() {
    String initial = "package a; func b() {\n fmt.Printl<caret> \n}";
    String after = "package a;\nimport \"fmt\" func b() {\n fmt.Println(<caret>) \n}";
    doTestExcluded(initial, after, "fm");
  }
}
