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

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public abstract class GoCompletionTestBase extends GoCodeInsightFixtureTestCase {
  @NotNull
  @Override
  protected String getBasePath() {
    return "completion";
  }

  protected void doTestCompletion() {
    myFixture.testCompletion(getTestName(true) + ".go", getTestName(true) + "_after.go");
  }

  protected enum CheckType {EQUALS, INCLUDES, EXCLUDES, ORDERED_EQUALS}

  private void doTestVariantsInner(@NotNull CompletionType type, int count, CheckType checkType, String... variants) {
    myFixture.complete(type, count);
    List<String> stringList = myFixture.getLookupElementStrings();

    assertNotNull("\nPossibly the single variant has been completed.\nFile after:\n" + myFixture.getFile().getText(), stringList);
    Collection<String> varList = ContainerUtil.newArrayList(variants);
    if (checkType == CheckType.ORDERED_EQUALS) {
      UsefulTestCase.assertOrderedEquals(stringList, variants);
    }
    else if (checkType == CheckType.EQUALS) {
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

  protected void doTestVariants(@NotNull String txt, @NotNull CompletionType type, int count, CheckType checkType, String... variants) {
    myFixture.configureByText(getDefaultFileName(), txt);
    failOnFileLoading();
    doTestVariantsInner(type, count, checkType, variants);
  }

  protected void doTestInclude(@NotNull String txt, String... variants) {
    doTestVariants(txt, CompletionType.BASIC, 1, CheckType.INCLUDES, variants);
  }

  protected void doTestExclude(@NotNull String txt, String... variants) {
    doTestVariants(txt, CompletionType.BASIC, 1, CheckType.EXCLUDES, variants);
  }

  protected void doTestEquals(@NotNull String txt, String... variants) {
    doTestVariants(txt, CompletionType.BASIC, 1, CheckType.EQUALS, variants);
  }

  protected void doCheckResult(@NotNull String before, @NotNull String after) {
    doCheckResult(before, after, (Character)null);
  }

  protected void doCheckResult(@NotNull String before, @NotNull String after, @Nullable Character c) {
    myFixture.configureByText(getDefaultFileName(), before);
    failOnFileLoading();
    myFixture.completeBasic();
    if (c != null) myFixture.type(c);
    myFixture.checkResult(after);
  }
  
  protected void doCheckResult(@NotNull String before, @NotNull String after, @NotNull String selectItem) {
    myFixture.configureByText(getDefaultFileName(), before);
    failOnFileLoading();
    myFixture.completeBasic();
    selectLookupItem(selectItem);
    myFixture.checkResult(after);
  }

  protected void selectLookupItem(@NotNull String selectItem) {
    LookupElement[] lookupElements = myFixture.getLookupElements();
    assertNotNull("Lookup is empty", lookupElements);
    LookupElement toSelect = null;
    for (LookupElement lookupElement : lookupElements) {
      if (selectItem.equals(lookupElement.getLookupString())) {
        toSelect = lookupElement;
        break;
      }
    }
    assertNotNull(selectItem + " not found in lookup", toSelect);
    myFixture.getLookup().setCurrentItem(toSelect);
    myFixture.type(Lookup.NORMAL_SELECT_CHAR);
  }

  protected void failOnFileLoading() {
    //((PsiManagerImpl)myFixture.getPsiManager()).setAssertOnFileLoadingFilter(VirtualFileFilter.ALL, getTestRootDisposable());
  }
  
  protected String getDefaultFileName() {
    return "a.go";
  }
}
