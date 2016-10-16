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

package com.goide.inspections.unresolved;

import com.goide.psi.GoVarDeclaration;
import com.goide.psi.GoVarDefinition;
import com.goide.quickfix.GoDeleteVarDefinitionQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoUnusedGlobalVariableInspection extends GoUnusedVariableInspection {
  @Override
  protected void reportError(@NotNull GoVarDefinition varDefinition, @NotNull ProblemsHolder holder) {
    holder.registerProblem(varDefinition, "Unused variable <code>#ref</code> #loc", ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                           new GoDeleteVarDefinitionQuickFix(varDefinition.getName()));
  }

  @Override
  protected boolean shouldValidate(@Nullable GoVarDeclaration varDeclaration) {
    return !super.shouldValidate(varDeclaration);
  }
}
