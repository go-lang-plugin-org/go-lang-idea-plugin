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
import com.goide.psi.GoType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class GoUnderlyingTypeTest extends GoCodeInsightFixtureTestCase {
  private void doTest(@NotNull String text, String expected) {
    myFixture.configureByText("a.go", "package a\n" + text);
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getEditor().getCaretModel().getOffset());
    GoType type = PsiTreeUtil.getParentOfType(element, GoType.class);
    assertNotNull(type);
    assertEquals(expected, type.getUnderlyingType().getText());
  }

  public void testT1()          { doTest("type <caret>T1 string", "string"); }
  public void testT1SOE()       { doTest("type <caret>T1 T2; type T2 T1", "T1 T2"); }
  public void testT2SOE()       { doTest("type <caret>T1 T2; type T2 T3; type T3 T2", "T2 T3"); }
  public void testT2()          { doTest("type T1 string; type <caret>T2 T1", "string"); }
  public void testT3()          { doTest("type T1 string; type T2 T1; type <caret>T3 []T1", "[]T1"); }
  public void testT4()          { doTest("type T1 string; type T2 T1; type T3 []T1; type <caret>T4 T3", "[]T1"); }
  public void testLiteralType() { doTest("type T1 string; type T4 <caret>[]T1", "[]T1"); }
}
