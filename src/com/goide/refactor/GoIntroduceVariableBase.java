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

package com.goide.refactor;

import com.goide.inspections.GoInspectionUtil;
import com.goide.psi.*;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.introduce.inplace.InplaceVariableIntroducer;
import com.intellij.refactoring.introduce.inplace.OccurrencesChooser;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class GoIntroduceVariableBase {
  @NotNull
  public static List<PsiElement> getOccurrences(@NotNull final PsiElement pattern, @Nullable PsiElement context) {
    if (context == null) return Collections.emptyList();
    final List<PsiElement> occurrences = ContainerUtil.newArrayList();
    PsiRecursiveElementVisitor visitor = new PsiRecursiveElementVisitor() {
      public void visitElement(@NotNull PsiElement element) {
        if (PsiEquivalenceUtil.areElementsEquivalent(element, pattern)) {
          occurrences.add(element);
          return;
        }
        super.visitElement(element);
      }
    };
    context.acceptChildren(visitor);
    return occurrences;
  }

  protected static void performOnElement(@NotNull final Project project,
                                         @NotNull final Editor editor,
                                         @NotNull final GoExpression expression) {
    if (GoInspectionUtil.getExpressionResultCount(expression) != 1) {
      showCannotPerform(project, editor, "Expression " + expression.getText() + " returns multiple values", null);
      return;
    }
    PsiElement statement = PsiTreeUtil.getParentOfType(expression, GoStatement.class);
    if (statement == null) {
      showCannotPerform(project, editor);
      return;
    }
    final List<PsiElement> occurrences = getOccurrences(expression, PsiTreeUtil.getParentOfType(statement, GoBlock.class));
    OccurrencesChooser.simpleChooser(editor).showChooser(expression, occurrences, new Pass<OccurrencesChooser.ReplaceChoice>() {
      @Override
      public void pass(OccurrencesChooser.ReplaceChoice choice) {
        performOnElementOccurrences(project, editor, expression, occurrences, choice == OccurrencesChooser.ReplaceChoice.ALL);
      }
    });
  }

  private static boolean isCommonAncestor(@NotNull PsiElement ancestor, @NotNull List<PsiElement> occurrences) {
    for (PsiElement element : occurrences) {
      if (!PsiTreeUtil.isAncestor(ancestor, element, false)) return false;
    }
    return true;
  }

  private static PsiElement findAnchor(@NotNull List<PsiElement> occurrences) {
    PsiElement statement = ContainerUtil.getFirstItem(occurrences);
    while (true) {
      statement = PsiTreeUtil.getParentOfType(statement, GoStatement.class);
      if (statement == null) return null;
      if (isCommonAncestor(statement.getParent(), occurrences)) return statement;
    }
  }

  private static void performOnElementOccurrences(@NotNull final Project project,
                                                  @NotNull final Editor editor,
                                                  @NotNull final GoExpression expression,
                                                  final List<PsiElement> occurrences, final boolean replaceAll) {
    final PsiElement anchor = replaceAll ? findAnchor(occurrences) : PsiTreeUtil.getParentOfType(expression, GoStatement.class);
    if (anchor == null) {
      showCannotPerform(project, editor, "Cannot Perform Refactoring in Current Context", null);
      return;
    }
    LinkedHashSet<String> suggestedNames = getSuggestedNames(expression);
    final String name = ContainerUtil.getFirstItem(suggestedNames);
    assert name != null;
    final List<PsiElement> newOccurrences = ContainerUtil.newArrayList();
    PsiElement statement = new WriteCommandAction<PsiElement>(project, anchor.getContainingFile()) {
      @Override
      protected void run(@NotNull Result<PsiElement> result) throws Throwable {
        PsiElement parent = anchor.getParent();
        GoStatement declaration = GoElementFactory.createVarDeclarationStatement(project, name, expression);
        PsiElement newLine = GoElementFactory.createNewLine(project);
        PsiElement added = parent.addBefore(declaration, parent.addBefore(newLine, anchor));
        if (replaceAll) {
          for (PsiElement occurrence : occurrences) {
            PsiElement occurrenceParent = occurrence.getParent();
            if (occurrenceParent instanceof GoParenthesesExpr) occurrence = occurrenceParent;
            PsiElement replaced = occurrence.replace(GoElementFactory.createVarReference(project, name));
            if (replaced != null) newOccurrences.add(replaced);
          }
        }
        else {
          PsiElement newExpression = GoElementFactory.createVarReference(project, name);
          PsiElement expressionParent = expression.getParent();
          PsiElement toReplace = expressionParent instanceof GoParenthesesExpr ? expressionParent : expression;
          toReplace.replace(newExpression);
        }
        result.setResult(added);
      }
    }.execute().getResultObject();

  GoVarDefinition var = PsiTreeUtil.findChildOfType(statement, GoVarDefinition.class);
    assert var != null;
    editor.getCaretModel().moveToOffset(var.getIdentifier().getTextOffset());
    GoInplaceVariableIntroducer introducer = new GoInplaceVariableIntroducer(var, editor, newOccurrences);
    introducer.performInplaceRefactoring(suggestedNames);
  }

  @NotNull
  private static LinkedHashSet<String> getSuggestedNames(GoExpression expression) {
    if (expression instanceof GoCallExpr) {
      List<String> names = ContainerUtil.newArrayList();
      GoReferenceExpression callReference = PsiTreeUtil.getChildOfType(expression, GoReferenceExpression.class);
      if (callReference != null) {
        String name = StringUtil.decapitalize(callReference.getIdentifier().getText());
        if (name.startsWith("get")) {
          name = name.substring(3);
        }
        else if (name.startsWith("is")) {
          name = name.substring(2);
        }
        for (int i = 0; i < name.length(); i++) {
          if (i == 0 || (Character.isLowerCase(name.charAt(i - 1)) && Character.isUpperCase(name.charAt(i)))) {
            names.add(StringUtil.decapitalize(name.substring(i)));
          }
        }
      }
      return ContainerUtil.newLinkedHashSet(ContainerUtil.reverse(names));
    }
    return ContainerUtil.newLinkedHashSet("i");
  }

  protected static void showCannotPerform(@NotNull Project project, @NotNull Editor editor) {
    showCannotPerform(project, editor, null, null);
  }

  protected static void showCannotPerform(@NotNull Project project, @NotNull Editor editor, String message, String title) {
    message = StringUtil.isEmpty(message) ? "Cannot Perform Refactoring" : message;
    title = StringUtil.isEmpty(title) ? "Cannot Perform Refactoring" : title;
    CommonRefactoringUtil.showErrorHint(project, editor, message, title, "refactoring.extractVariable");
  }

  protected static void smartIntroduce(@NotNull final Project project, @NotNull final Editor editor, @NotNull List<GoExpression> expressions) {
    IntroduceTargetChooser.showChooser(editor, expressions, new Pass<GoExpression>() {
      @Override
      public void pass(GoExpression expression) {
        performOnElement(project, editor, expression);
      }
    }, new Function<GoExpression, String>() {
      @Override
      public String fun(GoExpression expression) {
        return expression.getText();
      }
    });
  }

  private static GoExpression findParentExpression(@NotNull PsiElement element) {
    return PsiTreeUtil.getParentOfType(element, GoExpression.class);
  }

  private static List<GoExpression> collectNestedExpressions(@NotNull PsiElement element) {
    GoExpression expression = element instanceof GoExpression ? (GoExpression)element : findParentExpression(element);
    List<GoExpression> expressions = ContainerUtil.newArrayList();
    while (expression != null) {
      if (!(expression instanceof GoParenthesesExpr) && GoInspectionUtil.getExpressionResultCount(expression) == 1) {
        expressions.add(expression);
      }
      expression = findParentExpression(expression);
    }
    return expressions;
  }

  protected static List<GoExpression> collectExpressionsAtOffset(@NotNull final PsiFile file,
                                                                 @NotNull final Document document,
                                                                 int offset) {
    CharSequence text = document.getCharsSequence();
    if (offset >= text.length()) return ContainerUtil.emptyList();
    if (text.charAt(offset) == ')') offset--;
    PsiElement element = file.findElementAt(offset);
    if (element instanceof PsiWhiteSpace) element = file.findElementAt(offset - 1);
    if (element == null) return ContainerUtil.emptyList();
    return collectNestedExpressions(element);
  }

  protected static List<GoExpression> collectExpressionsInSelection(@NotNull final PsiFile file,
                                                                    @NotNull final SelectionModel selectionModel) {
    PsiElement element1 = file.findElementAt(selectionModel.getSelectionStart());
    PsiElement element2 = file.findElementAt(selectionModel.getSelectionEnd() - 1);
    if (element1 instanceof PsiWhiteSpace) element1 = file.findElementAt(element1.getTextRange().getEndOffset());
    if (element2 instanceof PsiWhiteSpace) element2 = file.findElementAt(element2.getTextRange().getStartOffset() - 1);
    if (element1 == null || element2 == null) return ContainerUtil.emptyList();
    PsiElement parent = PsiTreeUtil.findCommonParent(element1, element2);
    if (parent == null) return ContainerUtil.emptyList();
    return collectNestedExpressions(parent);
  }

  private static class GoInplaceVariableIntroducer extends InplaceVariableIntroducer<PsiElement> {
    private GoVarDefinition myTarget;

    public GoInplaceVariableIntroducer(GoVarDefinition target,
                                       Editor editor,
                                       List<PsiElement> occurrences) {
      super(target, editor, editor.getProject(), "Introduce Variable", ArrayUtil.toObjectArray(occurrences, PsiElement.class),
            null);
      myTarget = target;
    }

    @Nullable
    @Override
    protected PsiElement checkLocalScope() {
      return myTarget.getContainingFile();
    }
  }
}
