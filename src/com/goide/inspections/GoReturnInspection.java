/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

import com.goide.GoTypes;
import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GoReturnInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder,
                                     @SuppressWarnings({"UnusedParameters", "For future"}) @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitFunctionOrMethodDeclaration(@NotNull GoFunctionOrMethodDeclaration o) { // todo: extract common interface
        check(o.getSignature(), o.getBlock(), holder);
      }

      @Override
      public void visitFunctionLit(@NotNull GoFunctionLit o) {
        check(o.getSignature(), o.getBlock(), holder);
      }
    };
  }

  private static void check(@Nullable GoSignature signature, @Nullable GoBlock block, @NotNull ProblemsHolder holder) {
    if (block == null) return;
    GoResult result = signature != null ? signature.getResult() : null;
    if (result == null || isTerminating(block)) return;

    PsiElement brace = block.getRbrace();
    holder.registerProblem(brace == null ? block : brace, "Missing return at end of function",
                           brace == null ? new LocalQuickFix[]{} : new LocalQuickFix[]{new AddReturnFix(block)});
  }

  private static boolean isTerminating(@Nullable GoCompositeElement s) {
    if (s instanceof GoReturnStatement || s instanceof GoGoStatement) {
      return true;
    }
    else if (s instanceof GoSimpleStatement) {
      GoLeftHandExprList list = ((GoSimpleStatement)s).getLeftHandExprList();
      GoExpression expression = ContainerUtil.getFirstItem(list != null ? list.getExpressionList() : null);
      if (expression instanceof GoCallExpr && GoPsiImplUtil.isPanic((GoCallExpr)expression)) return true;
    }
    else if (s instanceof GoBlock) {
      return isTerminating(ContainerUtil.getLastItem(((GoBlock)s).getStatementList()));
    }
    else if (s instanceof GoIfStatement) {
      GoBlock block = ((GoIfStatement)s).getBlock();
      GoStatement st = ((GoIfStatement)s).getElseStatement();
      if (block != null && isTerminating(block) && st != null && isTerminating(st)) return true;
      return false;
    }
    else if (s instanceof GoElseStatement) {
      GoIfStatement ifStatement = ((GoElseStatement)s).getIfStatement();
      if (ifStatement != null) return isTerminating(ifStatement);
      GoBlock block = ((GoElseStatement)s).getBlock();
      if (block != null) return isTerminating(block);
      return false;
    }
    else if (s instanceof GoForStatement) {
      GoForStatement f = (GoForStatement)s;
      if (f.getExpression() == null && f.getForClause() == null && f.getRangeClause() == null) return true;
      return isTerminating(f.getBlock());
    }
    else if (s instanceof GoExprSwitchStatement) {
      boolean hasDefault = false;
      List<GoExprCaseClause> list = ((GoExprSwitchStatement)s).getExprCaseClauseList();
      for (GoExprCaseClause clause : list) {
        PsiElement def = clause.getDefault();
        if (def != null) hasDefault = true;
        GoStatement last = ContainerUtil.getLastItem(clause.getStatementList());
        if (last instanceof GoFallthroughStatement) continue;
        if (last == null || !isTerminating(last)) return false;
      }
      return hasDefault;
    }
    else if (s instanceof GoTypeSwitchStatement) { // todo: almost the same code 
      boolean hasDefault = false;
      List<GoTypeCaseClause> list = ((GoTypeSwitchStatement)s).getTypeCaseClauseList();
      for (GoTypeCaseClause clause : list) {
        GoTypeSwitchCase switchCase = clause.getTypeSwitchCase();
        PsiElement child = switchCase == null ? null : switchCase.getFirstChild();
        if (child != null && child.getNode().getElementType() == GoTypes.DEFAULT) {
          hasDefault = true;
        }
        GoStatement last = ContainerUtil.getLastItem(clause.getStatementList());
        if (last == null || !isTerminating(last)) return false;
      }
      return hasDefault;
    }
    else if (s instanceof GoSelectStatement) {
      GoSelectStatement selectStatement = (GoSelectStatement)s;
      for (GoCommClause clause : selectStatement.getCommClauseList()) {
        List<GoStatement> statements = clause.getStatementList();
        if (statements.size() == 0) {
          return false;
        }
        if (!isTerminating(statements.get(statements.size() - 1))) {
          return false;
        }
      }
      return true;
    }
    else if (s instanceof GoLabeledStatement) {
      GoLabeledStatement labeledStatement = (GoLabeledStatement)s;
      return isTerminating(labeledStatement.getStatement());
    }
    else if (s instanceof GoStatement && ((GoStatement)s).getBlock() != null) {
      return isTerminating(((GoStatement)s).getBlock());
    }
    return false;
  }

  public static class AddReturnFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    public AddReturnFix(@NotNull GoBlock block) {
      super(block);
    }

    @NotNull
    @Override
    public String getText() {
      return "Add return statement";
    }

    @NotNull
    @Override
    public String getFamilyName() {
      return "Function declaration";
    }

    @Override
    public void invoke(@NotNull Project project, 
                       @NotNull PsiFile file,
                       @Nullable("is null when called from inspection") Editor editor,
                       @NotNull PsiElement startElement, 
                       @NotNull PsiElement endElement) {
      if (!(file instanceof GoFile) || editor == null || !(startElement instanceof GoBlock)) return;

      PsiElement brace = ((GoBlock)startElement).getRbrace();
      if (brace == null) return;

      Template template = TemplateSettings.getInstance().getTemplateById("go_lang_add_return");
      if (template == null) return;
      int start = brace.getTextRange().getStartOffset();
      editor.getCaretModel().moveToOffset(start);
      editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
      template.setToReformat(true);
      TemplateManager.getInstance(project).startTemplate(editor, template, true, Collections.<String, String>emptyMap(), null);
    }
  }
}
