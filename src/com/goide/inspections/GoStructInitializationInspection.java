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

package com.goide.inspections;

import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.quickfix.GoReplaceWithNamedStructFieldQuickFix;
import com.goide.util.GoUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ui.SingleCheckboxOptionsPanel;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class GoStructInitializationInspection extends GoInspectionBase {
  public boolean reportImportedStructs;

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitCompositeLit(@NotNull GoCompositeLit o) {
        if (PsiTreeUtil.getParentOfType(o, GoReturnStatement.class, GoShortVarDeclaration.class, GoAssignmentStatement.class) == null) {
          return;
        }
        GoType refType = GoPsiImplUtil.getLiteralType(o);
        if (refType instanceof GoStructType) {
          processStructType((GoStructType)refType, o, holder);
        }
      }
    };
  }

  @Override
  public JComponent createOptionsPanel() {
    return new SingleCheckboxOptionsPanel("Report for local type definitions as well", this, "reportImportedStructs");
  }

  private static boolean hasAnonymousStructField(@NotNull List<GoFieldDeclaration> fields) {
    // TODO Anonymous structs have a more complex resolution process and can't be referred directly
    // Also quick fixing them would require producing a struct and add it
    for (GoFieldDeclaration field : fields) {
      if (field.getAnonymousFieldDefinition() != null) return true;
    }
    return false;
  }

  private void processStructType(@NotNull GoStructType structType,
                                 @NotNull GoCompositeLit compositeLit,
                                 @NotNull ProblemsHolder holder) {
    if (reportImportedStructs || GoUtil.inSamePackage(structType.getContainingFile(), compositeLit.getContainingFile())) {
      List<GoFieldDeclaration> fields = structType.getFieldDeclarationList();
      if (!hasAnonymousStructField(fields)) {
        processLiteralValue(holder, fields, compositeLit.getLiteralValue());
      }
    }
  }

  private static void processLiteralValue(@NotNull ProblemsHolder holder,
                                          @NotNull List<GoFieldDeclaration> structFields,
                                          @Nullable GoLiteralValue o) {
    if (o == null) return;
    List<GoElement> elemList = o.getElementList();
    for (int elemId = 0; elemId < elemList.size(); elemId++) {
      ProgressManager.checkCanceled();
      GoElement element = elemList.get(elemId);
      if (element.getKey() == null && elemId < structFields.size()) {
        String structFieldName = getFieldName(structFields.get(elemId));
        LocalQuickFix[] fixes = structFieldName != null
                                ? new LocalQuickFix[]{new GoReplaceWithNamedStructFieldQuickFix(structFieldName, element)}
                                : LocalQuickFix.EMPTY_ARRAY;
        holder.registerProblem(element, "Unnamed field initialization", ProblemHighlightType.GENERIC_ERROR_OR_WARNING, fixes);
      }
    }
  }

  @Nullable
  private static String getFieldName(@NotNull GoFieldDeclaration declaration) {
    List<GoFieldDefinition> list = declaration.getFieldDefinitionList();
    GoFieldDefinition fieldDefinition = ContainerUtil.getFirstItem(list);
    return fieldDefinition != null ? fieldDefinition.getIdentifier().getText() : null;
  }
}
