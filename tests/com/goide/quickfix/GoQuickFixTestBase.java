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

package com.goide.quickfix;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.codeInsight.intention.IntentionAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class GoQuickFixTestBase extends GoCodeInsightFixtureTestCase {
  protected void doTest(@NotNull String quickFixName) {
    doTest(quickFixName, false);
  }

  protected void doTest(@NotNull String quickFixName, boolean checkHighlighting) {
    String testName = getTestName(true);
    configure(checkHighlighting, testName);
    applySingleQuickFix(quickFixName);
    myFixture.checkResultByFile(testName + "-after.go", true);
  }

  protected void doTestNoFix(@NotNull String name) {
    doTestNoFix(name, false);
  }

  protected void doTestNoFix(@NotNull String name, boolean checkHighlighting) {
    configure(checkHighlighting, getTestName(true));
    List<IntentionAction> availableIntentions = myFixture.filterAvailableIntentions(name);
    assertEmpty(availableIntentions);
  }

  private void configure(boolean checkHighlighting, String testName) {
    if (checkHighlighting) {
      myFixture.testHighlighting(testName + ".go");
    }
    else {
      myFixture.configureByFile(testName + ".go");
      myFixture.doHighlighting();
    }
  }
}
