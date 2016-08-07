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

import com.goide.highlighting.exitpoint.GoBreakStatementExitPointHandler;
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

public class GoMissingReturnInspection extends GoInspectionBase {
  public static final String ADD_RETURN_STATEMENT_QUICK_FIX_NAME = "Add return statement";

  private static void check(@Nullable GoSignature signature, @Nullable GoBlock block, @NotNull ProblemsHolder holder) {
    if (block == null) return;
    GoResult result = signature != null ? signature.getResult() : null;
    if (result == null || result.isVoid() || isTerminating(block)) return;

    PsiElement brace = block.getRbrace();
    holder.registerProblem(brace == null ? block : brace, "Missing return at end of function",
                           brace == null ? new LocalQuickFix[]{} : new LocalQuickFix[]{new AddReturnFix(block)});
  }

  // https://tip.golang.org/ref/spec#Terminating_statements
  private static boolean isTerminating(@Nullable GoCompositeElement s) {
    if (s instanceof GoReturnStatement || s instanceof GoGotoStatement) {
      return true;
    }
    if (s instanceof GoSimpleStatement) {
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
      return block != null && isTerminating(block) && st != null && isTerminating(st);
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
      GoForClause forClause = f.getForClause();
      if (forClause != null && forClause.getExpression() != null || f.getExpression() != null || f.getRangeClause() != null) return false;
      GoBlock block = f.getBlock();
      return block == null || !hasReferringBreakStatement(f);
    }
    else if (s instanceof GoExprSwitchStatement) {
      return isTerminating((GoExprSwitchStatement)s, ((GoExprSwitchStatement)s).getExprCaseClauseList());
    }
    else if (s instanceof GoTypeSwitchStatement) {
      return isTerminating((GoTypeSwitchStatement)s, ((GoTypeSwitchStatement)s).getTypeCaseClauseList());
    }
    else if (s instanceof GoSelectStatement) {
      GoSelectStatement selectStatement = (GoSelectStatement)s;
      for (GoCommClause clause : selectStatement.getCommClauseList()) {
        List<GoStatement> statements = clause.getStatementList();
        if (hasReferringBreakStatement(selectStatement)) return false;
        if (!isTerminating(ContainerUtil.getLastItem(statements))) return false;
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

  private static boolean isTerminating(@NotNull GoSwitchStatement switchStatement, @NotNull List<? extends GoCaseClause> clauses) {
    boolean hasDefault = false;
    for (GoCaseClause clause : clauses) {
      hasDefault |= clause.getDefault() != null;
      List<GoStatement> statements = clause.getStatementList();
      if (hasReferringBreakStatement(switchStatement)) return false;
      GoStatement last = ContainerUtil.getLastItem(statements);
      if (!(last instanceof GoFallthroughStatement) && !isTerminating(last)) return false;
    }
    return hasDefault;
  }

  private static boolean hasReferringBreakStatement(@NotNull PsiElement breakStatementOwner) {
    return !GoPsiImplUtil.goTraverser().withRoot(breakStatementOwner).traverse().filter(GoBreakStatement.class).filter(statement -> {
      PsiElement owner = GoBreakStatementExitPointHandler.getBreakStatementOwnerOrResolve(statement);
      if (breakStatementOwner.equals(owner)) {
        return true;
      }
      if (owner instanceof GoLabelDefinition) {
        PsiElement parent = owner.getParent();
        if (parent instanceof GoLabeledStatement && breakStatementOwner.equals(((GoLabeledStatement)parent).getStatement())) {
          return true;
        }
      }
      return false;
    }).isEmpty();
  }

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
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

  private static class AddReturnFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    public AddReturnFix(@NotNull GoBlock block) {
      super(block);
    }

    @NotNull
    @Override
    public String getText() {
      return ADD_RETURN_STATEMENT_QUICK_FIX_NAME;
    }

    @NotNull
    @Override
    public String getFamilyName() {
      return getName();
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
      TemplateManager.getInstance(project).startTemplate(editor, template, true, Collections.emptyMap(), null);
    }
  }
}
