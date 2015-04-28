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
import com.goide.project.GoModuleLibrariesService;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.UsefulTestCase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class GoCompletionTestBase extends GoCodeInsightFixtureTestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    String url = myFixture.getTempDirFixture().getFile("..").getUrl();
    GoModuleLibrariesService.getInstance(myModule).setLibraryRootUrls(url);
  }

  @Override
  protected String getBasePath() {
    return "completion";
  }

  protected void doTestCompletion() {
    myFixture.testCompletion(getTestName(true) + ".go", getTestName(true) + "_after.go");
  }

  protected enum CheckType {EQUALS, INCLUDES, EXCLUDES}

  protected void doTestVariantsInner(CompletionType type, int count, CheckType checkType, String... variants) {
    myFixture.complete(type, count);
    List<String> stringList = myFixture.getLookupElementStrings();

    assertNotNull(
      "\nPossibly the single variant has been completed.\n" +
      "File after:\n" +
      myFixture.getFile().getText(),
      stringList);
    Collection<String> varList = new ArrayList<String>(Arrays.asList(variants));
    if (checkType == CheckType.EQUALS) {
      UsefulTestCase.assertSameElements(stringList, variants);
    }
    else if (checkType == CheckType.INCLUDES) {
      varList.removeAll(stringList);
      assertTrue("Missing variants: " + varList, varList.isEmpty());
    }
    else if (checkType == CheckType.EXCLUDES) {
      varList.retainAll(stringList);
      assertTrue("Unexpected variants: " + varList, varList.isEmpty());
    }
  }

  protected void doTestVariants(String txt, CompletionType type, int count, CheckType checkType, String... variants) {
    myFixture.configureByText("a.go", txt);
    failOnFileLoading();
    doTestVariantsInner(type, count, checkType, variants);
  }

  protected void doTestInclude(String txt, String... variants) {
    doTestVariants(txt, CompletionType.BASIC, 1, CheckType.INCLUDES, variants);
  }

  protected void doTestExclude(String txt, String... variants) {
    doTestVariants(txt, CompletionType.BASIC, 1, CheckType.EXCLUDES, variants);
  }

  protected void doTestEquals(String txt, String... variants) {
    doTestVariants(txt, CompletionType.BASIC, 1, CheckType.EQUALS, variants);
  }

  protected void doCheckResult(@NotNull String before, @NotNull String after) {
    doCheckResult(before, after, null);
  }

  protected void doCheckResult(@NotNull String before, @NotNull String after, @Nullable Character c) {
    myFixture.configureByText("a.go", before);
    failOnFileLoading();
    myFixture.completeBasic();
    if (c != null) myFixture.type(c);
    myFixture.checkResult(after);
  }

  protected void failOnFileLoading() {
    //((PsiManagerImpl)myFixture.getPsiManager()).setAssertOnFileLoadingFilter(VirtualFileFilter.ALL, getTestRootDisposable());
  }
}
