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

package com.goide.quickfix;

import com.goide.psi.GoConditionalExpr;
import com.goide.psi.GoStringLiteral;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static com.goide.psi.impl.GoElementFactory.createExpression;
import static com.goide.psi.impl.GoPsiImplUtil.isSingleCharLiteral;
import static java.lang.String.format;

public class GoConvertStringToByteQuickFix extends LocalQuickFixBase {
  public static final String NAME = "Convert string to byte";

  public GoConvertStringToByteQuickFix() {
    super(NAME);
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    PsiElement element = descriptor.getPsiElement();
    if (!(element instanceof GoConditionalExpr) || !element.isValid()) {
      return;
    }

    GoConditionalExpr expr = (GoConditionalExpr)element;
    GoStringLiteral literal = ContainerUtil.findInstance(Arrays.asList(expr.getLeft(), expr.getRight()), GoStringLiteral.class);
    if (literal == null || !isSingleCharLiteral(literal)) {
      return;
    }
    literal.replace(createExpression(project, extractSingleCharFromText(literal)));
  }

  @NotNull
  private static String extractSingleCharFromText(@NotNull GoStringLiteral element) {
    return format("'%s'", ElementManipulators.getValueText(element));
  }
}
