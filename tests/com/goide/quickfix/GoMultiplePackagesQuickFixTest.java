/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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
import org.jetbrains.annotations.NotNull;

public class GoMultiplePackagesQuickFixTest extends GoQuickFixTestBase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoMultiplePackagesInspection.class);
  }

  @NotNull
  @Override
  protected String getBasePath() {
    return "quickfixes/multiple-packages";
  }

  public void testMultiplePackagesQuickFix() {
    myFixture.configureByFile("c_test.go");
    myFixture.configureByFile("b.go");
    myFixture.configureByFile("b_test.go");
    myFixture.configureByFile("a.go");

    GoMultiplePackagesQuickFix.setTestingPackageName("a", getTestRootDisposable());
    applySingleQuickFix("Rename packages");

    myFixture.checkResultByFile("a.go", "a-after.go", true);
    myFixture.checkResultByFile("b.go", "b-after.go", true);
    myFixture.checkResultByFile("b_test.go", "b_test-after.go", true);
    myFixture.checkResultByFile("c_test.go", "c_test-after.go", true);
  }
}
