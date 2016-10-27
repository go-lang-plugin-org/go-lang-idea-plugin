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
import com.goide.psi.impl.GoElementFactory;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.util.GoUtil;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ui.SingleCheckboxOptionsPanel;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class GoStructInitializationInspection extends GoInspectionBase {
  public static final String REPLACE_WITH_NAMED_STRUCT_FIELD_FIX_NAME = "Replace with named struct field";
  public boolean reportLocalStructs;
  /**
   * @deprecated use reportLocalStructs
   */
  @SuppressWarnings("WeakerAccess") public Boolean reportImportedStructs;

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitLiteralValue(@NotNull GoLiteralValue o) {
        if (PsiTreeUtil.getParentOfType(o, GoReturnStatement.class, GoShortVarDeclaration.class, GoAssignmentStatement.class) == null) {
          return;
        }
        PsiElement parent = o.getParent();
        GoType refType = GoPsiImplUtil.getLiteralType(parent, false);
        if (refType instanceof GoStructType) {
          processStructType(holder, o, (GoStructType)refType);
        }
      }
    };
  }

  @Override
  public JComponent createOptionsPanel() {
    return new SingleCheckboxOptionsPanel("Report for local type definitions as well", this, "reportLocalStructs");
  }

  private void processStructType(@NotNull ProblemsHolder holder, @NotNull GoLiteralValue element, @NotNull GoStructType structType) {
    if (reportLocalStructs || !GoUtil.inSamePackage(structType.getContainingFile(), element.getContainingFile())) {
      processLiteralValue(holder, element, structType.getFieldDeclarationList());
    }
  }

  private static void processLiteralValue(@NotNull ProblemsHolder holder,
                                          @NotNull GoLiteralValue o,
                                          @NotNull List<GoFieldDeclaration> fields) {
    List<GoElement> vals = o.getElementList();
    for (int elemId = 0; elemId < vals.size(); elemId++) {
      ProgressManager.checkCanceled();
      GoElement element = vals.get(elemId);
      if (element.getKey() == null && elemId < fields.size()) {
        String structFieldName = getFieldName(fields.get(elemId));
        LocalQuickFix[] fixes = structFieldName != null ? new LocalQuickFix[]{new GoReplaceWithNamedStructFieldQuickFix(structFieldName)}
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

  private static class GoReplaceWithNamedStructFieldQuickFix extends LocalQuickFixBase {
    private String myStructField;

    public GoReplaceWithNamedStructFieldQuickFix(@NotNull String structField) {
      super(REPLACE_WITH_NAMED_STRUCT_FIELD_FIX_NAME);
      myStructField = structField;
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      PsiElement startElement = descriptor.getStartElement();
      if (startElement instanceof GoElement) {
        startElement.replace(GoElementFactory.createLiteralValueElement(project, myStructField, startElement.getText()));
      }
    }
  }

  @Override
  public void readSettings(@NotNull Element node) throws InvalidDataException {
    super.readSettings(node);
    if (reportImportedStructs != null) {
      reportLocalStructs = reportImportedStructs;
    }
  }

  @Override
  public void writeSettings(@NotNull Element node) throws WriteExternalException {
    reportImportedStructs = null;
    super.writeSettings(node);
  }
}
