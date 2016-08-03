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

package com.goide.completion;

import com.goide.SdkAware;
import com.goide.project.GoExcludedPathsSettings;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;

@SdkAware
public class GoExcludedPackagesTest extends GoCompletionTestBase {
  @Override
  protected void tearDown() throws Exception {
    GoExcludedPathsSettings.getInstance(getProject()).setExcludedPackages(ArrayUtil.EMPTY_STRING_ARRAY);
    super.tearDown();
  }

  private void doTestExcluded(@NotNull String initial, @NotNull String after, String... excludedPaths) {
    GoExcludedPathsSettings.getInstance(getProject()).setExcludedPackages(excludedPaths);
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
    String after = "package a;\n\nimport \"fmt\"\n\nfunc b() {\n fmt.Println(<caret>) \n}";
    doTestExcluded(initial, after, "fm");
  }
}
