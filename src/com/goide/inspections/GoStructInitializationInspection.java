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

package com.goide.inspections;

import com.goide.psi.*;
import com.goide.quickfix.GoReplaceWithNamedStructFieldQuickFix;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ui.SingleCheckboxOptionsPanel;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class GoStructInitializationInspection extends GoInspectionBase {

  @SuppressWarnings("WeakerAccess")
  public boolean reportImportedStructs;

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitCompositeLit(@NotNull GoCompositeLit o) {
        super.visitCompositeLit(o);

        if (PsiTreeUtil.getParentOfType(o, GoReturnStatement.class, GoShortVarDeclaration.class, GoAssignmentStatement.class) == null) {
          return;
        }

        if (!(o.getContainingFile() instanceof GoFile)) return;
        String myPackage = ((GoFile)o.getContainingFile()).getPackageName();

        List<GoFieldDeclaration> fields;
        GoType type = o.getType();
        if (type instanceof GoStructType) {
          if (!canProcess(myPackage, (GoStructType)type)) return;
          fields = ((GoStructType)type).getFieldDeclarationList();
          if (hasAnonymousStructField(fields)) return;
          processLiteralValue(holder, fields, o.getLiteralValue());
          return;
        }

        GoTypeReferenceExpression ref = o.getTypeReferenceExpression();
        if (ref == null || ref.resolveType() == null) return;
        GoType refType = ref.resolveType().getUnderlyingType();
        if (refType instanceof GoStructType) {
          if (!canProcess(myPackage, (GoStructType)refType)) return;
          fields = ((GoStructType)refType).getFieldDeclarationList();
          if (hasAnonymousStructField(fields)) return;
          processLiteralValue(holder, fields, o.getLiteralValue());
        }
        else if (refType instanceof GoArrayOrSliceType) {
          type = ((GoArrayOrSliceType)refType).getType();
          if (type == null || !(type.getUnderlyingType() instanceof GoStructType)) return;
          if (!canProcess(myPackage, (GoStructType)type.getUnderlyingType())) return;
          fields = ((GoStructType)type.getUnderlyingType()).getFieldDeclarationList();
          if (hasAnonymousStructField(fields)) return;
          for (GoElement element : o.getLiteralValue().getElementList()) {
            GoValue value = element.getValue();
            if (value == null || value.getExpression() != null || value.getLiteralValue() == null) continue;
            processLiteralValue(holder, fields, value.getLiteralValue());
          }
        }
      }
    };
  }

  @Override
  public JComponent createOptionsPanel() {
    return new SingleCheckboxOptionsPanel("Report for local type definitions as well", this, "reportImportedStructs");
  }

  private boolean canProcess(String myPackage, @NotNull GoStructType type) {
    if (!(type.getContainingFile() instanceof GoFile)) return false;
    String typePackage = ((GoFile)type.getContainingFile()).getPackageName();
    return reportImportedStructs || myPackage == typePackage;
  }

  private static boolean hasAnonymousStructField(@NotNull List<GoFieldDeclaration> fields) {
    // TODO Anonymous structs have a more complex resolution process and can't be referred directly
    // Also quick fixing them would require producing a struct and add it
    for (GoFieldDeclaration field : fields) {
      if (field.getAnonymousFieldDefinition() != null) return true;
    }
    return false;
  }

  private static void processLiteralValue(@NotNull ProblemsHolder holder,
                                          @NotNull List<GoFieldDeclaration> structFields,
                                          @NotNull GoLiteralValue o) {
    List<GoElement> elemList = o.getElementList();
    for (int elemId = 0; elemId < elemList.size(); elemId++) {
      GoElement element = elemList.get(elemId);
      if (element.getKey() == null) {
        String structFieldName = getFieldName(structFields.get(elemId));
        holder.registerProblem(
          element,
          "Unnamed field initialization",
          ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
          new GoReplaceWithNamedStructFieldQuickFix(structFieldName, element)
        );
      }
    }
  }

  private static String getFieldName(@NotNull GoFieldDeclaration declaration) {
    return declaration.getFieldDefinitionList().get(0).getIdentifier().getText();
  }
}
