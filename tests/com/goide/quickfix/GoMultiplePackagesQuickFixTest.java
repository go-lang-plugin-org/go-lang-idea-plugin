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

package com.goide.quickfix;

import com.goide.inspections.GoMultiplePackagesInspection;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.util.containers.ContainerUtil;

import java.util.List;

public class GoMultiplePackagesQuickFixTest extends GoQuickFixTestBase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoMultiplePackagesInspection.class);
  }

  @Override
  protected String getBasePath() {
    return "quickfixes/multiple-packages";
  }

  public void testMultiplePackagesQuickFix() {
    myFixture.configureByFile("a.go");
    myFixture.configureByFile("b.go");
    myFixture.configureByFile("b_test.go");

    List<IntentionAction> availableIntentions = myFixture.filterAvailableIntentions("Rename packages");
    IntentionAction action = ContainerUtil.getFirstItem(availableIntentions);
    assertNotNull(action);
    myFixture.launchAction(action);

    myFixture.checkResultByFile("a.go", "a-after.go", true);
    myFixture.checkResultByFile("b.go", "b-after.go", true);
    myFixture.checkResultByFile("b_test.go", "b_test-after.go", true);
  }
}
