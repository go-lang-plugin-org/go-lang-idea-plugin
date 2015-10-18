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

package com.goide.psi.impl;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.psi.GoVarDefinition;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class GoNamedElementTest extends GoCodeInsightFixtureTestCase{
  private <T> void doTestGetUseScope(@NotNull String text, @NotNull Class<T> scope) {
    myFixture.configureByText("a.go", text);
    PsiFile file = myFixture.getFile();
    GoVarDefinition var = PsiTreeUtil.findChildOfType(file, GoVarDefinition.class);
    assertNotNull(var);
    assertTrue(scope.isInstance(var.getUseScope()));
  }

  public void testGlobalVarScope() {
    doTestGetUseScope("package a; var b = 1", GlobalSearchScope.class);
  }

  public void testLocalVarScope() {
    doTestGetUseScope("package a; func a() {\n var b = 1 }", LocalSearchScope.class);
  }

}
