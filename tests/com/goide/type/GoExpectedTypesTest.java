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

package com.goide.type;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.psi.GoExpression;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.psi.impl.GoTypeUtil;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;

public class GoExpectedTypesTest extends GoCodeInsightFixtureTestCase {

  public void testAssignment() {
    doStatementTest("var a int; a = <selection>asd()</selection>", "int");
  }

  public void testTwoVariablesAssignment() {
    doStatementTest("var (a int; b string); a, b = <selection>asd()</selection>", "int, string; int");
  }

  public void testTwoVariablesAssignmentWithTeoRightExpressions() {
    doStatementTest("var (a int; b string); a, b = <selection>asd()</selection>, \"qwe\"", "int");
  }

  public void testTwoVariablesAssignmentWithFewLeftAndRightExpressions() {
    doStatementTest("var (a, c int; b string); a, b, c = 1, <selection>asd()</selection>, \"qwe\"", "string");
  }

  public void testShortVarDeclaration() {
    doStatementTest(" c := <selection>asd()</selection>", "interface{}");
  }

  public void testShortVarDeclarationWithTwoVariablesAtRight() {
    doStatementTest(" c, d := <selection>asd()</selection>", "interface{}, interface{}; interface{}");
  }

  public void testVarDeclaration() {
    doStatementTest(" var a int = <selection>asd()</selection>", "int");
  }

  public void testVarDeclarationWithStructTypeAndTwoVariablesAtRight() {
    doStatementTest(" var a, b struct{i int} = <selection>asd()</selection>", "struct{i int}, struct{i int}; struct{i int}");
  }

  public void testVarDeclWithTwoMembersAtRightAndLeft() {
    doStatementTest(" var a, b struct{i int} = struct{i int} {1}, <selection>asd()</selection>", "struct{i int}");
  }

  public void testCall() {
    doTopLevelTest(" func f(string){}; func _() { f(<selection>asd()</selection>) }", "string");
  }

  public void testCallWithThreeParametersInReceiver() {
    doTopLevelTest(" func f(string, int, int){}; func _() { f(<selection>asd()</selection>) }", "string, int, int; string");
  }

  public void testCallWithTwoArgumentsAndThreeParametersInReceiver() {
    doTopLevelTest(" func f(string, int, int){}; func _() { f(\"\", <selection>asd()</selection>) }", "int");
  }

  public void testCallWithNoParametersInReceiver() {
    doTopLevelTest(" func f(){}; func _() { f(<selection>asd()</selection>) }", "interface{}");
  }

  public void testCallWithNoParametersInReceiverAndThreeArguments() {
    doTopLevelTest(" func f(){}; func _() { f(1, 2, <selection>asd()</selection>) }", "interface{}");
  }

  public void testCallWithOneExpectedType() {
    doTopLevelTest(" func f(int, int){}; func _() { f(1,  <selection>asd()</selection>) }", "int");
  }

  public void testCallWithThreeExpectedTypes() {
    doTopLevelTest(" func f(int, int, int){}; func _() { f(<selection>asd()</selection>) }", "int, int, int; int");
  }

  public void testCallWithParamDefinition() {
    doTopLevelTest(" func f(i, j int, string, int){}; func _() { f(<selection>asd()</selection>) }", "int, int, string, int; int");
  }

  public void testRange() {
    doStatementTest("for _ = range <selection>asd()</selection> {}", "interface{}");
  }

  public void testRangeWithTwoArguments() {
    doStatementTest("for _, _ = range <selection>asd()</selection> {}", "interface{}");
  }

  public void testCaseStatement() {
    doStatementTest("var k int; switch k { case <selection>asd()</selection>: }", "int");
  }

  public void testCaseStatementWithoutArg() {
    doStatementTest("var k int; switch  { case <selection>asd()</selection>: }", "bool");
  }

  public void testCaseStatementOnStatementAndExpr() {
    doStatementTest("var k int; switch q:=1; q { case <selection>asd()</selection>: }", "int");
  }

  public void testSendStatementArg() {
    doStatementTest("var ch chan int; ch <- <selection>asd()</selection>", "int");
  }

  public void testSendStatementChan() {
    doStatementTest("var i string; <selection>asd()</selection> <- i", "chan string");
  }

  public void testSendStatementArgInSelect() {
    doStatementTest("var ch chan int; select { case ch <- <selection>asd()</selection> : }", "int");
  }

  public void testSendUnaryOperation() {
    doStatementTest("var i int; i = <-<selection>asd()</selection>", "chan int");
  }

  public void testSendUnaryOperationVarSpec() {
    doStatementTest("i := <-<selection>asd()</selection>", "chan interface{}");
  }

  public void testSendUnaryOperationNoExpectedType() {
    doStatementTest(" <-<selection>asd()</selection>", "chan interface{}");
  }

  public void testRecvStatement() {
    doStatementTest("var i string; select { case <-<selection>asd()</selection> : }", "chan interface{}");
  }

  public void testRecvStatementOnFunc() {
    doStatementTest("var i string; select { case <selection>asd()</selection> : }", "interface{}");
  }

  public void testRecvStatementOnVarAssign() {
    doStatementTest("var i string; select { case i = <selection>asd()</selection> : }", "string");
  }

  public void testRecvStatementOnShortVarAssign() {
    doStatementTest("select { case i := <selection>asd()</selection> : }", "interface{}");
  }

  private void doTopLevelTest(@NotNull String text, @NotNull String expectedTypeText) {
    myFixture.configureByText("a.go", "package a;" + text);
    PsiElement elementAt;
    SelectionModel selectionModel = myFixture.getEditor().getSelectionModel();
    if (selectionModel.hasSelection()) {
      PsiElement left = myFixture.getFile().findElementAt(selectionModel.getSelectionStart());
      PsiElement right = myFixture.getFile().findElementAt(selectionModel.getSelectionEnd() - 1);
      assertNotNull(left);
      assertNotNull(right);
      elementAt = PsiTreeUtil.findCommonParent(left, right);
    }
    else {
      elementAt = myFixture.getFile().findElementAt(myFixture.getEditor().getCaretModel().getOffset());
    }
    assertNotNull(elementAt);

    GoExpression typeOwner = PsiTreeUtil.getNonStrictParentOfType(elementAt, GoExpression.class);
    assertNotNull("Cannot find type owner. Context element: " + elementAt.getText(), typeOwner);


    assertEquals(expectedTypeText, StringUtil.join(GoTypeUtil.getExpectedTypes(typeOwner), GoPsiImplUtil.GET_TEXT_FUNCTION, "; "));
  }

  private void doStatementTest(@NotNull String text, @NotNull String expectedTypeText) {
    doTopLevelTest("func _() {\n" + text + "\n}", expectedTypeText);
  }

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
  }
}
