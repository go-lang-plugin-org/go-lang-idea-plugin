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
import com.goide.SdkAware;
import com.goide.psi.GoType;
import com.goide.psi.GoTypeOwner;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

@SdkAware
public class GoTypeResolveTest extends GoCodeInsightFixtureTestCase {
  public void testAnon() {
    doTopLevelTest("type A struct{};type E A;type B struct{ E };func (e E) foo() {};func main() { b := B{}; b.<caret>E }", "E");
  }

  public void testAnon2() {
    doTopLevelTest("type A struct{};type E A;type B struct{ *E };func (e E) foo() {};func main() { b := B{}; b.<caret>E }", "*E");
  }

  public void testTypeSwitchDeclaration() {
    doStatementTest("switch fo<caret>o := \"hello\".(type) {}", "string");
  }

  public void testTypeSwitchUsageInContext() {
    doStatementTest("switch foo := \"hello\".(type) { case bool:\n println(fo<caret>o)\n}", "bool");
  }

  public void testWrappedSlice() {
    doTopLevelTest("type Foo int[]\nfunc _() { var foo Foo\nb<caret>ar := foo[2:9]", "Foo");
  }

  public void testSlice() {
    doStatementTest("var foo []int\nb<caret>ar := foo[2:9]", "[]int");
  }

  public void testRangeOverString() {
    doStatementTest("for _, fo<caret>o := range \"hello\" {}", "int32");
  }

  public void testIndexExpressionOfString() {
    doStatementTest("foo := \"hello\"\na := <selection>foo[0]</selection>", "byte");
  }

  public void testIndexExpressionOfStringLiteral() {
    doExpressionTest("<selection>\"hello\"[0]</selection>", "byte");
  }

  public void testIndexExpressionOfPointer() {
    doStatementTest("var buckhash *[20]*int\nfo<caret>o := buckhash[0]", "*int");
  }

  public void testNestedTypeSwitchUsageInContext() {
    doStatementTest("var p interface{}\n" +
                    "switch foo := p.(type) {\n" +
                    "case int:\n" +
                    "  switch p.(type) {\n" +
                    "  case bool:" +
                    "    println(f<caret>oo)\n" +
                    "  }\n" +
                    "}", "int");
  }

  public void testIndexExpression() {
    doTopLevelTest("type foo string; var bar []foo; func _() { println(<selection>bar[0]</selection>) }", "foo");
    doTopLevelTest("var bar []string; func _() { println(<selection>bar[0]</selection>) }", "string");
  }
  
  public void testFuncLiteral() {
    doTopLevelTest("func voidFunction(a int) {} func _() { <selection>voidFunction</selection>() }", "func (a int)");
  }
  
  public void testCallExpression() {
    doTopLevelTest("func voidFunction() int {} func _() { <selection>voidFunction()</selection> }", "int");
  }
  
  public void testCallExpressionOnVoidFunction() {
    doTopLevelTest("func voidFunction() {} func _() { <selection>voidFunction()</selection> }", "<unknown>");
  }

  public void testCallExpressionOnVoidFunction2() {
    doTopLevelTest("func voidFunction() () {} func _() { <selection>voidFunction()</selection> }", "<unknown>");
  }

  private void doTopLevelTest(@NotNull String text, @NotNull String expectedTypeText) {
    myFixture.configureByText("a.go", "package a;" + text);
    PsiElement elementAt = findElementAtCaretOrInSelection();

    GoTypeOwner typeOwner = PsiTreeUtil.getNonStrictParentOfType(elementAt, GoTypeOwner.class);
    assertNotNull("Cannot find type owner. Context element: " + elementAt.getText(), typeOwner);

    GoType type = typeOwner.getGoType(null);
    assertEquals(expectedTypeText, type == null ? "<unknown>" : type.getText());
  }

  private void doStatementTest(@NotNull String text, @NotNull String expectedTypeText) {
    doTopLevelTest("func _() {\n" + text + "\n}", expectedTypeText);
  }

  private void doExpressionTest(@NotNull String text, @NotNull String expectedTypeText) {
    doStatementTest("a := " + text, expectedTypeText);
  }
}
