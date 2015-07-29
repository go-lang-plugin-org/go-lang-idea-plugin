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
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.RefactoringBundle;
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
    PsiElement statement = PsiTreeUtil.getParentOfType(expression, GoStatement.class);
    if (statement == null) {
      showCannotPerform(project, editor, RefactoringBundle.message("refactoring.introduce.context.error"));
      return;
    }
    final List<PsiElement> occurrences = getOccurrences(expression, PsiTreeUtil.getParentOfType(statement, GoBlock.class));
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      performOnElementOccurrences(project, editor, expression, occurrences, true);
      return;
    }
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
    while (statement != null) {
      statement = PsiTreeUtil.getParentOfType(statement, GoStatement.class);
      if (statement != null && isCommonAncestor(statement.getParent(), occurrences)) return statement;
    }
    return null;
  }

  private static void performOnElementOccurrences(@NotNull final Project project,
                                                  @NotNull final Editor editor,
                                                  @NotNull final GoExpression expression,
                                                  final List<PsiElement> occurrences, final boolean replaceAll) {
    final PsiElement anchor = replaceAll ? findAnchor(occurrences) : PsiTreeUtil.getParentOfType(expression, GoStatement.class);
    if (anchor == null) {
      showCannotPerform(project, editor, RefactoringBundle.message("refactoring.introduce.context.error"));
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
        GoStatement declaration = GoElementFactory.createVarDeclarationStatement(project, name, expression, true);
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
    PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());

    GoVarDefinition var = PsiTreeUtil.findChildOfType(statement, GoVarDefinition.class);
    assert var != null;
    editor.getCaretModel().moveToOffset(var.getIdentifier().getTextOffset());
    GoInplaceVariableIntroducer introducer = new GoInplaceVariableIntroducer(var, editor, newOccurrences);
    introducer.performInplaceRefactoring(suggestedNames);
  }

  private static LinkedHashSet<String> getNamesInContext(PsiElement context) {
    // todo rewrite with resolve
    if (context == null) return ContainerUtil.newLinkedHashSet();
    final LinkedHashSet<String> names = ContainerUtil.newLinkedHashSet();

    for (GoNamedElement namedElement : PsiTreeUtil.findChildrenOfType(context, GoNamedElement.class)) {
      names.add(namedElement.getName());
    }

    names.addAll(((GoFile)context.getContainingFile()).getImportMap().keySet());

    GoFunctionDeclaration functionDeclaration = PsiTreeUtil.getParentOfType(context, GoFunctionDeclaration.class);
    GoSignature signature = PsiTreeUtil.getChildOfType(functionDeclaration, GoSignature.class);
    for (GoParamDefinition param : PsiTreeUtil.findChildrenOfType(signature, GoParamDefinition.class)) {
      names.add(param.getName());
    }
    return names;
  }

  @NotNull
  private static LinkedHashSet<String> getSuggestedNames(GoExpression expression) {
    LinkedHashSet<String> usedNames = getNamesInContext(PsiTreeUtil.getParentOfType(expression, GoBlock.class));
    LinkedHashSet<String> names = ContainerUtil.newLinkedHashSet();
    if (expression instanceof GoCallExpr) {
      GoReferenceExpression callReference = PsiTreeUtil.getChildOfType(expression, GoReferenceExpression.class);
      if (callReference != null) {
        String name = StringUtil.decapitalize(callReference.getIdentifier().getText());
        for (String candidate : NameUtil.getSuggestionsByName(name, "", "", false, false, false)) {
          if (!usedNames.contains(candidate)) names.add(candidate);
        }
      }
    }
    if (names.isEmpty()) {
      if (usedNames.contains("i")) {
        int counter = 1;
        while (usedNames.contains("i" + counter)) counter++;
        names.add("i" + counter);
      }
      else {
        names.add("i");
      }
    }
    return names;
  }

  protected static void showCannotPerform(@NotNull Project project, @NotNull Editor editor, String message) {
    message = RefactoringBundle.getCannotRefactorMessage(message);
    CommonRefactoringUtil
      .showErrorHint(project, editor, message, RefactoringBundle.getCannotRefactorMessage(null), "refactoring.extractVariable");
  }

  protected static void smartIntroduce(@NotNull final Project project,
                                       @NotNull final Editor editor,
                                       @NotNull List<GoExpression> expressions) {
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

  private static List<GoExpression> collectNestedExpressions(@NotNull Project project,
                                                             @NotNull Editor editor,
                                                             @NotNull PsiElement element) {
    GoExpression expression = element instanceof GoExpression ? (GoExpression)element : findParentExpression(element);
    if (expression instanceof GoParenthesesExpr) expression = ((GoParenthesesExpr)expression).getExpression();
    List<GoExpression> expressions = ContainerUtil.newArrayList();
    GoExpression invalidExpression = null;
    while (expression != null) {
      if (!(expression instanceof GoParenthesesExpr)) {
        if (GoInspectionUtil.getExpressionResultCount(expression) == 1) {
          expressions.add(expression);
        }
        else {
          invalidExpression = expression;
        }
      }
      expression = findParentExpression(expression);
    }
    if (expressions.isEmpty()) {
      if (invalidExpression == null) {
        showCannotPerform(project, editor, "No expression found.");
      }
      else {
        int resultCount = GoInspectionUtil.getExpressionResultCount(invalidExpression);
        showCannotPerform(project, editor, "Expression " + invalidExpression.getText() +
                                           (resultCount == 0 ? " doesn't return a value." : " returns multiple values."));
      }
    }
    return expressions;
  }

  protected static List<GoExpression> collectExpressionsAtOffset(@NotNull final PsiFile file,
                                                                 @NotNull final Project project,
                                                                 @NotNull final Editor editor,
                                                                 int offset) {
    PsiElement element = file.findElementAt(offset);
    GoParenthesesExpr parenthesesParent = PsiTreeUtil.getParentOfType(element, GoParenthesesExpr.class);
    if (element instanceof PsiWhiteSpace || (parenthesesParent != null && parenthesesParent.getRparen() == element)) {
      element = file.findElementAt(offset - 1);
    }
    if (element == null) return ContainerUtil.emptyList();
    return collectNestedExpressions(project, editor, element);
  }

  protected static List<GoExpression> collectExpressionsInSelection(@NotNull final PsiFile file,
                                                                    @NotNull final Project project,
                                                                    @NotNull final Editor editor,
                                                                    @NotNull final SelectionModel selectionModel) {
    PsiElement element1 = file.findElementAt(selectionModel.getSelectionStart());
    PsiElement element2 = file.findElementAt(selectionModel.getSelectionEnd() - 1);
    if (element1 instanceof PsiWhiteSpace) element1 = file.findElementAt(element1.getTextRange().getEndOffset());
    if (element2 instanceof PsiWhiteSpace) element2 = file.findElementAt(element2.getTextRange().getStartOffset() - 1);
    if (element1 == null || element2 == null) return ContainerUtil.emptyList();
    PsiElement parent = PsiTreeUtil.findCommonParent(element1, element2);
    if (parent == null) return ContainerUtil.emptyList();
    return collectNestedExpressions(project, editor, parent);
  }

  private static class GoInplaceVariableIntroducer extends InplaceVariableIntroducer<PsiElement> {
    public GoInplaceVariableIntroducer(GoVarDefinition target,
                                       Editor editor,
                                       List<PsiElement> occurrences) {
      super(target, editor, editor.getProject(), "Introduce Variable", ArrayUtil.toObjectArray(occurrences, PsiElement.class), null);
    }

    @Nullable
    @Override
    protected PsiElement checkLocalScope() {
      return myElementToRename.getContainingFile();
    }
  }
}
