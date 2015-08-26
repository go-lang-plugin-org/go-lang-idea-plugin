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

import com.goide.inspections.GoSelfImportInspection;
import com.intellij.testFramework.LightProjectDescriptor;

public class GoSelfImportQuickFixTest extends GoQuickFixTestBase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoSelfImportInspection.class);
    setUpProjectSdk();
  }

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }

  @Override
  protected String getBasePath() {
    return "quickfixes/self-import";
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  public void testRemoveSelfImport() {
    myFixture.configureByFile("b.go");
    //applySingleQuickFix("Remove self import");
    //myFixture.checkResultByFile("b-after.go");
  }
}
