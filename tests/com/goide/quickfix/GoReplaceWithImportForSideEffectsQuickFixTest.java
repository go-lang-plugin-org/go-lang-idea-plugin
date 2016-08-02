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

import com.goide.SdkAware;
import com.goide.inspections.GoUnusedImportInspection;

@SdkAware
public class GoReplaceWithImportForSideEffectsQuickFixTest extends GoQuickFixTestBase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoUnusedImportInspection.class);
  }

  public void testSimple() {
    myFixture.configureByText("a.go", "package pack; import \"fm<caret>t\"");
    applySingleQuickFix("Import for side-effects");
    myFixture.checkResult("package pack; import _ \"fmt\"");
  }
}
