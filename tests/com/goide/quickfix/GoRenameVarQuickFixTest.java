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

import com.goide.inspections.unresolved.GoUnusedVariableInspection;
import org.jetbrains.annotations.NotNull;

public class GoRenameVarQuickFixTest extends GoQuickFixTestBase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoUnusedVariableInspection.class);
  }

  @NotNull
  @Override
  protected String getBasePath() {
    return "quickfixes/rename-var";
  }

  public void testSimple()                                    { doTest(GoRenameToBlankQuickFix.NAME);      }
  public void testProhibited()                                { doTestNoFix(GoRenameToBlankQuickFix.NAME); }
  public void testRenameAndReplaceWithAssignment_1()          { doTest(GoRenameToBlankQuickFix.NAME);      }
  public void testRenameAndReplaceWithAssignment_2()          { doTest(GoRenameToBlankQuickFix.NAME);      }
  public void testRenameAndReplaceWithAssignment_3()          { doTest(GoRenameToBlankQuickFix.NAME);      }
  public void testRenameAndReplaceWithAssignmentOnRange()     { doTest(GoRenameToBlankQuickFix.NAME);      }
}
