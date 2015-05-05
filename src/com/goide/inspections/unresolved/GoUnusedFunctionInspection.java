/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

package com.goide.inspections.unresolved;

import com.goide.GoConstants;
import com.goide.inspections.GoDeleteQuickFix;
import com.goide.inspections.GoInspectionBase;
import com.goide.inspections.GoRenameToBlankQuickFix;
import com.goide.psi.GoFile;
import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoVisitor;
import com.goide.runconfig.testing.GoTestFinder;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

public class GoUnusedFunctionInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder,
                                     @SuppressWarnings({"UnusedParameters", "For future"}) @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitFunctionDeclaration(@NotNull GoFunctionDeclaration o) {
        if (o.isBlank()) return;
        GoFile file = o.getContainingFile();
        String name = o.getName();
        if (GoConstants.MAIN.equals(file.getPackageName()) && GoConstants.MAIN.equals(name)) return;
        if (GoConstants.INIT.equals(name)) return;
        if (GoTestFinder.isTestFile(file) && (GoTestFinder.isTestFunctionName(name) || GoTestFinder.isBenchmarkFunctionName(name))) return;
        Query<PsiReference> search = ReferencesSearch.search(o, o.getUseScope());
        if (search.findFirst() == null) {
          PsiElement id = o.getIdentifier();
          TextRange range = TextRange.from(id.getStartOffsetInParent(), id.getTextLength());
          holder.registerProblem(o, "Unused function " + "'" + name + "'", ProblemHighlightType.LIKE_UNUSED_SYMBOL, range,
                                 new GoDeleteQuickFix("Delete function '" + name + "'"), new GoRenameToBlankQuickFix(o));
        }
      }
    };
  }
}
