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

package com.goide.psi.impl;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.psi.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

public class GoPsiImplUtilTest extends GoCodeInsightFixtureTestCase {
  public void testGetLocalPackageNameDash() {
    assertEquals("test_directory", GoPsiImplUtil.getLocalPackageName("test-directory"));
  }

  public void testGetLocalPackageNameDigitAtBeginning() {
    assertEquals("_23abc", GoPsiImplUtil.getLocalPackageName("123abc"));
  }

  public void testGetLocalPackageNameUnderscore() {
    assertEquals("_", GoPsiImplUtil.getLocalPackageName("_"));
  }
  
  public void testGetLocalPackageNameForPath() {
    assertEquals("test_directory", GoPsiImplUtil.getLocalPackageName("path/test-directory"));
  }

  public void testAddVarSpec() {
    myFixture.configureByText("a.go", "package main\n\nvar (\n" +
                                      "  fo<caret>o int\n" +
                                      ")");
    GoVarDeclaration declaration = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDeclaration.class);
    assertNotNull(declaration);
    declaration.addSpec("bar", "string", "`1`", null);
    myFixture.checkResult("package main\n\nvar (\n  foo int\nbar string = `1`\n)");
  }

  public void testAddVarSpecWithoutParens() {
    myFixture.configureByText("a.go", "package main\n\nvar fo<caret>o int\n");
    GoVarDeclaration declaration = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDeclaration.class);
    assertNotNull(declaration);
    declaration.addSpec("bar", "string", "`1`", null);
    myFixture.checkResult("package main\n\nvar (foo int\nbar string = `1`\n)\n");
  }

  public void testAddVarSpecNoNewLines() {
    myFixture.configureByText("a.go", "package main\n\nvar (fo<caret>o int\n)");
    GoVarDeclaration declaration = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDeclaration.class);
    assertNotNull(declaration);
    declaration.addSpec("bar", "string", "`1`", null);
    myFixture.checkResult("package main\n\nvar (foo int\nbar string = `1`\n)");
  }

  public void testAddVarSpecWithAnchor() {
    myFixture.configureByText("a.go", "package main\n\nvar (\n" +
                                      "  fo<caret>o int\n" +
                                      ")");
    GoVarSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarSpec.class);
    assertNotNull(spec);
    GoVarDeclaration declaration = (GoVarDeclaration)spec.getParent();
    declaration.addSpec("bar", "string", "`1`", spec);
    myFixture.checkResult("package main\n\nvar (\n  bar string = `1`\nfoo int\n)");
  }

  public void testAddVarSpecWithoutParensWithAnchor() {
    myFixture.configureByText("a.go", "package main\n\nvar fo<caret>o int\n");
    GoVarSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarSpec.class);
    assertNotNull(spec);
    GoVarDeclaration declaration = (GoVarDeclaration)spec.getParent();
    declaration.addSpec("bar", "string", "`1`", spec);
    myFixture.checkResult("package main\n\nvar (\nbar string = `1`\nfoo int)\n");
  }

  public void testAddVarSpecNoNewLinesWithAnchor() {
    myFixture.configureByText("a.go", "package main\n\nvar (fo<caret>o int\n)");
    GoVarSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarSpec.class);
    assertNotNull(spec);
    GoVarDeclaration declaration = (GoVarDeclaration)spec.getParent();
    declaration.addSpec("bar", "string", "`1`", spec);
    myFixture.checkResult("package main\n\nvar (\nbar string = `1`\nfoo int\n)");
  }

  public void testAddConstSpec() {
    myFixture.configureByText("a.go", "package main\n\nconst (\n" +
                                      "  fo<caret>o int\n" +
                                      ")");
    GoConstDeclaration declaration = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstDeclaration.class);
    assertNotNull(declaration);
    declaration.addSpec("bar", "string", "`1`", null);
    myFixture.checkResult("package main\n\nconst (\n  foo int\nbar string = `1`\n)");
  }

  public void testAddConstSpecWithoutParens() {
    myFixture.configureByText("a.go", "package main\n\nconst fo<caret>o int\n");
    GoConstDeclaration declaration = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstDeclaration.class);
    assertNotNull(declaration);
    declaration.addSpec("bar", "string", "`1`", null);
    myFixture.checkResult("package main\n\nconst (foo int\nbar string = `1`\n)\n");
  }

  public void testAddConstSpecNoNewLines() {
    myFixture.configureByText("a.go", "package main\n\nconst (fo<caret>o int\n)");
    GoConstDeclaration declaration = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstDeclaration.class);
    assertNotNull(declaration);
    declaration.addSpec("bar", "string", "`1`", null);
    myFixture.checkResult("package main\n\nconst (foo int\nbar string = `1`\n)");
  }

  public void testAddConstSpecWithAnchor() {
    myFixture.configureByText("a.go", "package main\n\nconst (\n" +
                                      "  fo<caret>o int\n" +
                                      ")");
    GoConstSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstSpec.class);
    assertNotNull(spec);
    GoConstDeclaration declaration = (GoConstDeclaration)spec.getParent();
    declaration.addSpec("bar", "string", "`1`", spec);
    myFixture.checkResult("package main\n\nconst (\n  bar string = `1`\nfoo int\n)");
  }

  public void testAddConstSpecWithoutParensWithAnchor() {
    myFixture.configureByText("a.go", "package main\n\nconst fo<caret>o int\n");
    GoConstSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstSpec.class);
    assertNotNull(spec);
    GoConstDeclaration declaration = (GoConstDeclaration)spec.getParent();
    declaration.addSpec("bar", "string", "`1`", spec);
    myFixture.checkResult("package main\n\nconst (\nbar string = `1`\nfoo int)\n");
  }

  public void testAddConstSpecNoNewLinesWithAnchor() {
    myFixture.configureByText("a.go", "package main\n\nconst (fo<caret>o int\n)");
    GoConstSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstSpec.class);
    assertNotNull(spec);
    GoConstDeclaration declaration = (GoConstDeclaration)spec.getParent();
    declaration.addSpec("bar", "string", "`1`", spec);
    myFixture.checkResult("package main\n\nconst (\nbar string = `1`\nfoo int\n)");
  }

  public void testGetTypeOfSingleVarDefinition() {
    myFixture.configureByText("a.go", "package main\n\n var f<caret>oo int");
    GoVarDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDefinition.class);
    assertNotNull(definition);                      
    GoType type = definition.findSiblingType();
    assertNotNull(type);
    assertEquals("int", type.getText());
  }

  public void testGetTypeOfMultipleVarDefinition() {
    myFixture.configureByText("a.go", "package main\n\n var fo<caret>o, bar int");
    GoVarDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDefinition.class);
    assertNotNull(definition);
    GoType type = definition.findSiblingType();
    assertNotNull(type);
    assertEquals("int", type.getText());
  }

  public void testGetTypeOfSingleConstDefinition() {
    myFixture.configureByText("a.go", "package main\n\n const fo<caret>o int = 1");
    GoConstDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstDefinition.class);
    assertNotNull(definition);
    GoType type = definition.findSiblingType();
    assertNotNull(type);
    assertEquals("int", type.getText());
  }

  public void testGetTypeOfMultipleConstDefinition() {
    myFixture.configureByText("a.go", "package main\n\n const fo<caret>o, bar int = 1, 2");
    GoConstDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstDefinition.class);
    assertNotNull(definition);
    GoType type = definition.findSiblingType();
    assertNotNull(type);
    assertEquals("int", type.getText());
  }
  
  public void testGetValueOfVarDefinitionInSwitch() {
    myFixture.configureByText("a.go", "package main\n\n func _() { bar := nil; switch fo<caret>o := bar.(type){}}");
    GoVarDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDefinition.class);
    assertNotNull(definition);
    GoExpression value = definition.getValue();
    assertNotNull(value);
    assertEquals("bar", value.getText());
  }
  
  public void testGetValueOfSingleVarDefinition() {
    myFixture.configureByText("a.go", "package main\n\n var fo<caret>o int = 1");
    GoVarDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDefinition.class);
    assertNotNull(definition);
    GoExpression value = definition.getValue();
    assertNotNull(value);
    assertEquals("1", value.getText());
  }
  
  public void testGetValueOfMultipleVarDefinition_1() {
    myFixture.configureByText("a.go", "package main\n\n var fo<caret>o, bar int = 1, 2");
    GoVarDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDefinition.class);
    assertNotNull(definition);
    GoExpression value = definition.getValue();
    assertNotNull(value);
    assertEquals("1", value.getText());
  }
  
  public void testGetValueOfMultipleVarDefinition_2() {
    myFixture.configureByText("a.go", "package main\n\n var foo, b<caret>ar int = 1, 2");
    GoVarDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDefinition.class);
    assertNotNull(definition);
    GoExpression value = definition.getValue();
    assertNotNull(value);
    assertEquals("2", value.getText());
  }
  
  public void testGetValueOfMultipleVarDefinitionWithoutValues() {
    myFixture.configureByText("a.go", "package main\n\n var f<caret>oo, bar int");
    GoVarDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDefinition.class);
    assertNotNull(definition);
    assertNull(definition.getValue());
  }
  
  public void testGetValueOfMultipleInvalidVarDefinition() {
    myFixture.configureByText("a.go", "package main\n\n var foo, b<caret>ar int = 1");
    GoVarDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDefinition.class);
    assertNotNull(definition);
    assertNull(definition.getValue());
  }
  
  public void testGetValueOfSingleConstDefinition() {
    myFixture.configureByText("a.go", "package main\n\n const fo<caret>o int = 1");
    GoConstDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstDefinition.class);
    assertNotNull(definition);
    GoExpression value = definition.getValue();
    assertNotNull(value);
    assertEquals("1", value.getText());
  }
  
  public void testGetValueOfMultipleConstDefinition_1() {
    myFixture.configureByText("a.go", "package main\n\n const fo<caret>o, bar int = 1, 2");
    GoConstDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstDefinition.class);
    assertNotNull(definition);
    GoExpression value = definition.getValue();
    assertNotNull(value);
    assertEquals("1", value.getText());
  }
  
  public void testGetValueOfMultipleConstDefinition_2() {
    myFixture.configureByText("a.go", "package main\n\n const foo, b<caret>ar int = 1, 2");
    GoConstDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstDefinition.class);
    assertNotNull(definition);
    GoExpression value = definition.getValue();
    assertNotNull(value);
    assertEquals("2", value.getText());
  }
  
  public void testDeleteSingleVarDefinition() {
    myFixture.configureByText("a.go", "package main\n\n var b<caret>ar int = 1, 2");
    GoVarDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDefinition.class);
    assertNotNull(definition);
    GoVarSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarSpec.class);
    assertNotNull(spec);
    spec.deleteDefinition(definition);
    myFixture.checkResult("package main\n\n ");
  }

  public void testDeleteFirstMultipleVarDefinitionWithValue() {
    myFixture.configureByText("a.go", "package main\n\n var fo<caret>o, bar int = 1, 2");
    GoVarDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDefinition.class);
    assertNotNull(definition);
    GoVarSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarSpec.class);
    assertNotNull(spec);
    spec.deleteDefinition(definition);
    myFixture.checkResult("package main\n\n var bar int =  2");
  }
  
  public void testDeleteMiddleMultipleVarDefinitionWithValue() {
    myFixture.configureByText("a.go", "package main\n\n var buzz, fo<caret>o, bar int = 1, 2, 3");
    GoVarDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDefinition.class);
    assertNotNull(definition);
    GoVarSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarSpec.class);
    assertNotNull(spec);
    spec.deleteDefinition(definition);
    myFixture.checkResult("package main\n\n var buzz , bar int = 1 , 3");
  }
  
  public void testDeleteLastMultipleVarDefinitionWithValue() {
    myFixture.configureByText("a.go", "package main\n\n var foo, b<caret>ar int = 1, 2");
    GoVarDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDefinition.class);
    assertNotNull(definition);
    GoVarSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarSpec.class);
    assertNotNull(spec);
    spec.deleteDefinition(definition);
    myFixture.checkResult("package main\n\n var foo  int = 1 ");
  }

  public void testDeleteLastMultipleVarDefinitionWithoutValue() {
    myFixture.configureByText("a.go", "package main\n\n var foo, b<caret>ar int = 1");
    GoVarDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDefinition.class);
    assertNotNull(definition);
    GoVarSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarSpec.class);
    assertNotNull(spec);
    spec.deleteDefinition(definition);
    myFixture.checkResult("package main\n\n var foo  int = 1");
  }
  
  public void testDeleteFirstMultipleVarDefinitionWithoutValue() {
    myFixture.configureByText("a.go", "package main\n\n var f<caret>oo, bar int = 1");
    GoVarDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarDefinition.class);
    assertNotNull(definition);
    GoVarSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoVarSpec.class);
    assertNotNull(spec);
    spec.deleteDefinition(definition);
    myFixture.checkResult("package main\n\n var bar int  ");
  }
  
  public void testDeleteSingleConstDefinition() {
    myFixture.configureByText("a.go", "package main\n\n const b<caret>ar int = 1, 2");
    GoConstDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstDefinition.class);
    assertNotNull(definition);
    GoConstSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstSpec.class);
    assertNotNull(spec);
    spec.deleteDefinition(definition);
    myFixture.checkResult("package main\n\n ");
  }

  public void testDeleteFirstMultipleConstDefinitionWithValue() {
    myFixture.configureByText("a.go", "package main\n\n const fo<caret>o, bar int = 1, 2");
    GoConstDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstDefinition.class);
    assertNotNull(definition);
    GoConstSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstSpec.class);
    assertNotNull(spec);
    spec.deleteDefinition(definition);
    myFixture.checkResult("package main\n\n const bar int =  2");
  }
  
  public void testDeleteMiddleMultipleConstDefinitionWithValue() {
    myFixture.configureByText("a.go", "package main\n\n const buzz, fo<caret>o, bar int = 1, 2, 3");
    GoConstDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstDefinition.class);
    assertNotNull(definition);
    GoConstSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstSpec.class);
    assertNotNull(spec);
    spec.deleteDefinition(definition);
    myFixture.checkResult("package main\n\n const buzz , bar int = 1 , 3");
  }
  
  public void testDeleteLastMultipleConstDefinitionWithValue() {
    myFixture.configureByText("a.go", "package main\n\n const foo, b<caret>ar int = 1, 2");
    GoConstDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstDefinition.class);
    assertNotNull(definition);
    GoConstSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstSpec.class);
    assertNotNull(spec);
    spec.deleteDefinition(definition);
    myFixture.checkResult("package main\n\n const foo  int = 1 ");
  }

  public void testDeleteLastMultipleConstDefinitionWithoutValue() {
    myFixture.configureByText("a.go", "package main\n\n const foo, b<caret>ar int = 1");
    GoConstDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstDefinition.class);
    assertNotNull(definition);
    GoConstSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstSpec.class);
    assertNotNull(spec);
    spec.deleteDefinition(definition);
    myFixture.checkResult("package main\n\n const foo  int = 1");
  }
  
  public void testDeleteFirstMultipleConstDefinitionWithoutValue() {
    myFixture.configureByText("a.go", "package main\n\n const f<caret>oo, bar int = 1");
    GoConstDefinition definition = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstDefinition.class);
    assertNotNull(definition);
    GoConstSpec spec = PsiTreeUtil.getNonStrictParentOfType(myFixture.getElementAtCaret(), GoConstSpec.class);
    assertNotNull(spec);
    spec.deleteDefinition(definition);
    myFixture.checkResult("package main\n\n const bar int  ");
  }

  public void testGoIndexOrSliceExprGetIndices() {
    PsiFile file = myFixture.configureByText("a.go", "package main\n var a []int\n var b = a<caret>[1]");
    GoIndexOrSliceExpr index = PsiTreeUtil.getParentOfType(file.findElementAt(myFixture.getCaretOffset()), GoIndexOrSliceExpr.class);
    assertNotNull(index);
    assertEquals("1", index.getIndices().first.getText());
  }

  @Override
  protected boolean isWriteActionRequired() {
    return true;
  }
}
