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

package com.goide.codeInsight.imports;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.inspections.unresolved.GoUnresolvedReferenceInspection;
import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl;


public class GoReferenceImporterTest extends GoCodeInsightFixtureTestCase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
    myFixture.enableInspections(GoUnresolvedReferenceInspection.class);
    ((CodeInsightTestFixtureImpl)myFixture).canChangeDocumentDuringHighlighting(true);
  }

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  private void doTestAddOnTheFly(boolean goOnTheFlyEnabled, boolean javaOnTheFlyEnabled) {
    boolean defaultJavaOnTheFly = CodeInsightSettings.getInstance().ADD_UNAMBIGIOUS_IMPORTS_ON_THE_FLY;
    try {
      CodeInsightSettings.getInstance().ADD_UNAMBIGIOUS_IMPORTS_ON_THE_FLY = javaOnTheFlyEnabled;
      GoCodeInsightSettings.getInstance().setAddUnambiguousImportsOnTheFly(goOnTheFlyEnabled);
      DaemonCodeAnalyzerSettings.getInstance().setImportHintEnabled(true);

      String initial = "package a; func a() {\n fmt.Println() <caret> \n}";
      myFixture.configureByText(getTestName(true) + ".go", initial);

      myFixture.doHighlighting();
      myFixture.doHighlighting();

      String after = "package a;\nimport \"fmt\" func a() {\n fmt.Println() <caret> \n}";
      String result = goOnTheFlyEnabled && javaOnTheFlyEnabled ? after : initial;
      myFixture.checkResult(result);
    }
    finally {
      CodeInsightSettings.getInstance().ADD_UNAMBIGIOUS_IMPORTS_ON_THE_FLY = defaultJavaOnTheFly;
    }
  }

  public void testOnTheFlyEnabled()  { doTestAddOnTheFly(true, true);  }
  public void testOnTheFlyDisabled() { doTestAddOnTheFly(false, true); }
  public void testOnTheFlyEnabledJavaOnTheFlyDisabled()  { doTestAddOnTheFly(true, false);  }
  public void testOnTheFlyDisabledJavaOnTheFlyDisabled() { doTestAddOnTheFly(false, false); }
}
