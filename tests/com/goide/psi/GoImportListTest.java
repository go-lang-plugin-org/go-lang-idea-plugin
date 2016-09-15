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

package com.goide.psi;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.openapi.command.WriteCommandAction;
import org.jetbrains.annotations.NotNull;

public class GoImportListTest extends GoCodeInsightFixtureTestCase {
  public void testAddImportToEmptyList()                                { doAddImportTest(); }
  public void testAddImportToListWithMultiSpecDeclaration()             { doAddImportTest(); }
  public void testAddImportToListWithSingleSpecDeclaration()            { doAddImportTest(); }
  public void testAddImportToListWithSingleSpecDeclarationWithParens()  { doAddImportTest(); }
  public void testAddImportToEmptyListBeforeFunction()                  { doAddImportTest(); }
  public void testAddImportBeforeFunction()                             { doAddImportTest(); }
  public void testDoNotModifyCImport_1()                                { doAddImportTest(); }
  public void testDoNotModifyCImport_2()                                { doAddImportTest(); }
  public void testInvalidImport()                                       { doAddImportTest(); }
  public void testInvalidImport2()                                      { doAddImportTest(); }
  
  private void doAddImportTest() {
    myFixture.configureByFile(getTestName(true) + ".go");
    WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () -> {
      ((GoFile)myFixture.getFile()).addImport("package/path", null);
    });
    myFixture.checkResultByFile(getTestName(true) + "_after.go");
  }

  @NotNull
  @Override
  protected String getBasePath() {
    return "psi/importDeclaration";
  }
}