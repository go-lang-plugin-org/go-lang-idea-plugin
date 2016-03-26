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
import com.goide.psi.GoType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class GoTypeResolveTest extends GoCodeInsightFixtureTestCase {
  private void doTest(@NotNull String text, String expected) {
    myFixture.configureByText("a.go", "package a;" + text);
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getEditor().getCaretModel().getOffset());
    GoExpression e = PsiTreeUtil.getParentOfType(element, GoExpression.class);
    assertNotNull(e);
    GoType type = e.getGoType(null);
    assertEquals(expected, type == null ? "<null>" : type.getText());
  }

  public void testAnon() { doTest("type A struct{};type E A;type B struct{ E };func (e E) foo() {};func main() { b := B{}; b.<caret>E }", "E A"); }
}
