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

package com.goide.inspections.suppression;

import com.goide.inspections.GoMultiplePackagesInspection;
import com.goide.inspections.GoUnusedImportInspection;
import com.goide.inspections.unresolved.GoUnresolvedReferenceInspection;
import com.goide.inspections.unresolved.GoUnusedGlobalVariableInspection;
import com.goide.inspections.unresolved.GoUnusedVariableInspection;
import com.goide.quickfix.GoQuickFixTestBase;
import org.jetbrains.annotations.NotNull;

public class GoSuppressionFixTest extends GoQuickFixTestBase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoUnusedImportInspection.class);
    myFixture.enableInspections(GoUnresolvedReferenceInspection.class);
    myFixture.enableInspections(GoUnusedGlobalVariableInspection.class);
    myFixture.enableInspections(GoUnusedVariableInspection.class);
    myFixture.enableInspections(GoMultiplePackagesInspection.class);
  }

  public void testImportStatement() {
    doTest("Suppress for import");
  }

  public void testFirstImportStatement() {
    doTest("Suppress for import");
  }

  public void testFunctionSuppressionFix() {
    doTest("Suppress for function");
  }

  public void testFunctionSuppressionFixWithExistingComment() {
    doTest("Suppress for function");
  }

  public void testStatementSuppressionFix() {
    doTest("Suppress for statement");
  }

  public void testStatementSuppressionFixWithExistingComment() {
    doTest("Suppress for statement");
  }

  public void testSwitchCaseSuppressionFix() {
    doTest("Suppress all inspections for case");
  }

  public void testSwitchCaseSuppressionFixWithExistingComment() {
    doTest("Suppress all inspections for case");
  }

  public void testSelectCaseSuppressionFix() {
    doTest("Suppress all inspections for case");
  }

  public void testSelectCaseSuppressionFixWithExistingComment() {
    doTest("Suppress all inspections for case");
  }

  public void testVariableDeclarationSuppressionFix() {
    doTest("Suppress for variable");
  }

  public void testVariableDeclarationSuppressionFixWithExistingComment() {
    doTest("Suppress for variable");
  }

  public void testFunctionAllSuppressionFix() {
    doTest("Suppress all inspections for function");
  }

  public void testFunctionAllSuppressionFixWithExistingComment() {
    doTest("Suppress all inspections for function");
  }

  public void testStatementAllSuppressionFix() {
    doTest("Suppress all inspections for statement");
  }

  public void testStatementAllSuppressionFixWithExistingComment() {
    doTest("Suppress all inspections for statement");
  }

  public void testSwitchCaseAllSuppressionFix() {
    doTest("Suppress all inspections for case");
  }

  public void testSwitchCaseAllSuppressionFixWithExistingComment() {
    doTest("Suppress all inspections for case");
  }

  public void testSelectCaseAllSuppressionFix() {
    doTest("Suppress all inspections for case");
  }

  public void testSelectCaseAllSuppressionFixWithExistingComment() {
    doTest("Suppress all inspections for case");
  }

  public void testVariableDeclarationAllSuppressionFix() {
    doTest("Suppress all inspections for variable");
  }

  public void testVariableDeclarationAllSuppressionFixWithExistingComment() {
    doTest("Suppress all inspections for variable");
  }

  public void testInnerVariableDeclarationSuppressionFix() {
    doTestNoFix("Suppress for variable");
  }

  public void testInnerVariableDeclarationSuppressionFix2() {
    doTest("Suppress for statement");
  }

  public void testInnerVariableDeclarationSuppressionFix3() {
    doTest("Suppress for function");
  }

  public void testCommClauseStatementSuppressionFix() {
    doTest("Suppress all inspections for statement");
  }

  public void testPackageClause() {
    myFixture.configureByText("a.go", "package somePackage");
    doTest("Suppress for package statement");
  }

  public void testPackageClauseSuppressAll() {
    myFixture.configureByText("a.go", "package somePackage");
    doTest("Suppress all inspections for package statement");
  }

  public void testSuppressedNestedSelect() {
    myFixture.testHighlighting(getTestName(true) + ".go");
  }

  @NotNull
  @Override
  protected String getBasePath() {
    return "inspections/suppression/fix";
  }

  @Override
  protected void doTest(@NotNull String quickFixName) {
    super.doTest(quickFixName);
    myFixture.testHighlighting(String.format("%s-after-highlighting.go", getTestName(true)));
  }
}
