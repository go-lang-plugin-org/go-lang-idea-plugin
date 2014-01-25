/*
 * Copyright 2012-2013 Sergey Ignatov
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
import com.intellij.util.containers.ContainerUtil;

import java.util.List;

public abstract class GoQuickFixTestBase extends GoCodeInsightFixtureTestCase {
  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  protected void doTest(String quickFixName) {
    final String testName = getTestName(true);
    myFixture.configureByFile(testName + ".go");
    List<IntentionAction> availableIntentions = myFixture.filterAvailableIntentions(quickFixName);
    IntentionAction action = ContainerUtil.getFirstItem(availableIntentions);
    assertNotNull(action);
    myFixture.launchAction(action);
    String after = String.format("%s-after.go", testName);
    myFixture.checkResultByFile(after);
  }
}
