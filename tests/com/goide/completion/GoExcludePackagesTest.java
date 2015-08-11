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
import com.goide.codeInsight.imports.GoCodeInsightSettings;
import com.intellij.testFramework.LightProjectDescriptor;

public class GoExcludePackagesTest extends GoCodeInsightFixtureTestCase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
  }

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }

  public void testExcludedPackageCompletion() {
    GoCodeInsightSettings settings = GoCodeInsightSettings.getInstance();
    String[] oldExcludedSettings = settings.getExcludedPackages();
    settings.setExcludedPackages("fmt");

    String initial = "package a; func b() {\n fmt.Printl<caret> \n}";
    myFixture.configureByText("a.go", initial);
    myFixture.completeBasic();
    myFixture.checkResult(initial);
    settings.setExcludedPackages(oldExcludedSettings);
  }
}
