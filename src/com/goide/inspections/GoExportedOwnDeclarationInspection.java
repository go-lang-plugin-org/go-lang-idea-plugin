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
import com.goide.psi.impl.GoElementFactory;
import com.intellij.codeInspection.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Part of the golint tool
 * <p/>
 * https://github.com/golang/lint/blob/32a87160691b3c96046c0c678fe57c5bef761456/lint.go#L827
 */
public class GoExportedOwnDeclarationInspection extends GoInspectionBase {
  public static final String QUICK_FIX_NAME = "Extract to own declaration...";

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitConstDeclaration(@NotNull GoConstDeclaration o) {
        if (!(o.getParent() instanceof GoFile)) {
          return;
        }

        for (int i = 0; i < o.getConstSpecList().size(); i++) {
          int index = 0;
          for (GoConstDefinition constDefinition : o.getConstSpecList().get(i).getConstDefinitionList()) {
            checkElement(holder, "const", constDefinition, index);
            index++;
          }
        }
      }

      @Override
      public void visitVarDeclaration(@NotNull GoVarDeclaration o) {
        if (!(o.getParent() instanceof GoFile)) {
          return;
        }

        for (int i = 0; i < o.getVarSpecList().size(); i++) {
          int index = 0;
          for (GoVarDefinition varDefinition : o.getVarSpecList().get(i).getVarDefinitionList()) {
            checkElement(holder, "var", varDefinition, index);
            index++;
          }
        }
      }
    };
  }

  public static void checkElement(ProblemsHolder holder, String type, GoNamedElement namedElement, int index) {
    if (index < 1 || !namedElement.isPublic()) {
      return;
    }

    String errorText = "Exported " + type + " '" + namedElement.getName() + "' should have its own declaration";
    holder.registerProblem(namedElement, errorText, ProblemHighlightType.WEAK_WARNING, new MyLocalQuickFixBase(index));
  }

  private static class MyLocalQuickFixBase extends LocalQuickFixBase {
    private int index;

    public MyLocalQuickFixBase(int index) {
      super(QUICK_FIX_NAME);
      this.index = index;
    }

    @Override
    public void applyFix(@NotNull final Project project, @NotNull ProblemDescriptor descriptor) {
      final PsiElement element = descriptor.getPsiElement();
      if (!element.isValid()) return;
      new WriteCommandAction.Simple(project, getName(), element.getContainingFile()) {
        @Override
        protected void run() throws Throwable {
          List<GoExpression> elementValueList;
          String elementType = "";
          if (element instanceof GoConstDefinition) {
            elementValueList = ((GoConstSpec)element.getParent()).getExpressionList();
            GoType elemType = ((GoConstSpec)element.getParent()).getType();
            if (elemType != null) {
              elementType = elemType.getText();
            }
          }
          else if (element instanceof GoVarDefinition) {
            elementValueList = ((GoVarSpec)element.getParent()).getExpressionList();
            GoType elemType = ((GoVarSpec)element.getParent()).getType();
            if (elemType != null) {
              elementType = elemType.getText();
            }
          }
          else {
            return;
          }

          if (!elementValueList.isEmpty() &&
              elementValueList.size() <= index) {
            return;
          }

          PsiElement elementSpec = element.getParent();
          PsiElement newElement;
          PsiElement afterElement;
          if (!elementValueList.isEmpty()) {
            PsiElement elementValue = elementValueList.get(index);
            if (element instanceof GoConstDefinition) {
              newElement = GoElementFactory.createConstSpec(project, element.getText(), elementType, elementValue.getText());
            }
            else {
              newElement = GoElementFactory.createVarSpec(project, element.getText(), elementType, elementValue.getText());
            }
          }
          else {
            if (element instanceof GoConstDefinition) {
              newElement = GoElementFactory.createConstSpec(project, element.getText(), elementType, "");
            }
            else {
              newElement = GoElementFactory.createVarSpec(project, element.getText(), elementType, "");
            }
          }

          afterElement = getAfterElement(element);
          elementSpec.addAfter(newElement, afterElement);
          elementSpec.addAfter(GoElementFactory.createNewLine(project), afterElement);

          if (elementSpec instanceof GoConstSpec) {
            List<GoConstDefinition> elementList = ((GoConstSpec)elementSpec).getConstDefinitionList();
            elementSpec.deleteChildRange(elementList.get(index - 1).getNextSibling(), elementList.get(index));
            elementValueList = ((GoConstSpec)elementSpec).getExpressionList();
          }
          else {
            List<GoVarDefinition> elementList = ((GoVarSpec)elementSpec).getVarDefinitionList();
            elementSpec.deleteChildRange(elementList.get(index - 1).getNextSibling(), elementList.get(index));
            elementValueList = ((GoVarSpec)elementSpec).getExpressionList();
          }

          if (!elementValueList.isEmpty()) {
            elementSpec.deleteChildRange(elementValueList.get(index - 1).getNextSibling(), elementValueList.get(index));
          }

          PsiElement elementDeclaration = elementSpec.getParent();
          if (elementDeclaration instanceof GoConstDeclaration &&
              newElement instanceof GoConstSpec &&
              ((GoConstDeclaration)elementDeclaration).getConstSpecList().size() == 1) {

            List<GoConstSpec> elementList = ((GoConstDeclaration)elementDeclaration).getConstSpecList();
            elementDeclaration.replace(GoElementFactory.createConstDeclaration(project, elementList));
          }
          else if (elementDeclaration instanceof GoVarDeclaration &&
                   newElement instanceof GoVarSpec &&
                   ((GoVarDeclaration)elementDeclaration).getVarSpecList().size() == 1) {

            List<GoVarSpec> elementList = ((GoVarDeclaration)elementDeclaration).getVarSpecList();
            elementDeclaration.replace(GoElementFactory.createVarDeclaration(project, elementList));
          }
        }
      }.execute();
    }

    private static PsiElement getAfterElement(PsiElement element) {
      PsiElement elementParent = element.getParent();

      if (element instanceof GoConstDefinition) {
        if (!((GoConstSpec)elementParent).getExpressionList().isEmpty()) {
          return ContainerUtil.getLastItem(((GoConstSpec)elementParent).getExpressionList());
        }

        return ((GoConstSpec)elementParent).getType() == null
               ? ContainerUtil.getLastItem(((GoConstSpec)elementParent).getConstDefinitionList())
               : ((GoConstSpec)elementParent).getType();
      }

      if (!((GoVarSpec)elementParent).getExpressionList().isEmpty()) {
        return ContainerUtil.getLastItem(((GoVarSpec)elementParent).getExpressionList());
      }

      return ((GoVarSpec)elementParent).getType() == null
             ? ContainerUtil.getLastItem(((GoVarSpec)elementParent).getVarDefinitionList())
             : ((GoVarSpec)elementParent).getType();
    }
  }
}
