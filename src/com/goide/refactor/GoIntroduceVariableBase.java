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

package com.goide.refactor;

import com.goide.inspections.GoInspectionUtil;
import com.goide.psi.*;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
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
  protected static void performAction(@NotNull final GoIntroduceOperation operation) {
    SelectionModel selectionModel = operation.getEditor().getSelectionModel();
    boolean hasSelection = selectionModel.hasSelection();
    GoExpression expression =
      hasSelection ? findExpressionInSelection(operation.getFile(), selectionModel.getSelectionStart(), selectionModel.getSelectionEnd())
                   : findExpressionAtOffset(operation);
    if (expression instanceof GoParenthesesExpr) expression = ((GoParenthesesExpr)expression).getExpression();
    if (expression == null) {
      String message =
        RefactoringBundle.message(hasSelection ? "selected.block.should.represent.an.expression" : "refactoring.introduce.selection.error");
      showCannotPerform(operation, message);
      return;
    }
    if (PsiTreeUtil.getParentOfType(expression, GoStatement.class) == null) {
      showCannotPerform(operation, RefactoringBundle.message("refactoring.introduce.context.error"));
      return;
    }
    List<GoExpression> expressions = collectNestedExpressions(expression);
    if (expressions.isEmpty()) {
      int resultCount = GoInspectionUtil.getExpressionResultCount(expression);
      showCannotPerform(operation, "Expression " + expression.getText() + "()" +
                                   (resultCount == 0 ? " doesn't return a value." : " returns multiple values."));
      return;
    }
    if (expressions.size() == 1 || hasSelection || ApplicationManager.getApplication().isUnitTestMode()) {
      //noinspection ConstantConditions
      operation.setExpression(ContainerUtil.getFirstItem(expressions));
      performOnElement(operation);
    }
    else {
      IntroduceTargetChooser.showChooser(operation.getEditor(), expressions, new Pass<GoExpression>() {
        @Override
        public void pass(@NotNull GoExpression expression) {
          operation.setExpression(expression);
          performOnElement(operation);
        }
      }, new Function<GoExpression, String>() {
        @Override
        public String fun(@NotNull GoExpression expression) {
          return expression.getText();
        }
      });
    }
  }

  @Nullable
  public static GoExpression findExpressionInSelection(@NotNull PsiFile file, int start, int end) {
    return PsiTreeUtil.findElementOfClassAtRange(file, start, end, GoExpression.class);
  }

  @Nullable
  private static GoExpression findExpressionAtOffset(@NotNull GoIntroduceOperation operation) {
    PsiFile file = operation.getFile();
    int offset = operation.getEditor().getCaretModel().getOffset();

    GoExpression expr = PsiTreeUtil.getNonStrictParentOfType(file.findElementAt(offset), GoExpression.class);
    GoExpression preExpr = PsiTreeUtil.getNonStrictParentOfType(file.findElementAt(offset - 1), GoExpression.class);
    if (expr == null || preExpr != null && PsiTreeUtil.isAncestor(expr, preExpr, false)) return preExpr;
    return expr;
  }

  @NotNull
  private static List<GoExpression> collectNestedExpressions(@Nullable GoExpression expression) {
    List<GoExpression> expressions = ContainerUtil.newArrayList();
    while (expression != null) {
      if (!(expression instanceof GoParenthesesExpr) && GoInspectionUtil.getExpressionResultCount(expression) == 1 &&
          !(expression instanceof GoReferenceExpression && expression.getParent() instanceof GoCallExpr)) {
        expressions.add(expression);
      }
      expression = PsiTreeUtil.getParentOfType(expression, GoExpression.class);
    }
    return expressions;
  }

  protected static void performOnElement(@NotNull final GoIntroduceOperation operation) {
    GoExpression expression = operation.getExpression();
    LinkedHashSet<String> suggestedNames = getSuggestedNames(expression);
    operation.setSuggestedNames(suggestedNames);
    operation.setOccurrences(GoRefactoringUtil.getLocalOccurrences(expression));

    Editor editor = operation.getEditor();
    if (editor.getSettings().isVariableInplaceRenameEnabled()) {
      //noinspection ConstantConditions
      operation.setName(ContainerUtil.getFirstItem(suggestedNames));
      if (ApplicationManager.getApplication().isUnitTestMode()) {
        performInplaceIntroduce(operation);
        return;
      }
      OccurrencesChooser.simpleChooser(editor)
        .showChooser(expression, operation.getOccurrences(), new Pass<OccurrencesChooser.ReplaceChoice>() {
          @Override
          public void pass(OccurrencesChooser.ReplaceChoice choice) {
            operation.setReplaceAll(choice == OccurrencesChooser.ReplaceChoice.ALL);
            performInplaceIntroduce(operation);
          }
        });
    }
    else {
      GoIntroduceVariableDialog dialog = new GoIntroduceVariableDialog(operation);
      if (dialog.showAndGet()) {
        operation.setName(dialog.getName());
        operation.setReplaceAll(dialog.getReplaceAll());
        performReplace(operation);
      }
    }
  }

  private static void performInplaceIntroduce(@NotNull GoIntroduceOperation operation) {
    performReplace(operation);
    new GoInplaceVariableIntroducer(operation).performInplaceRefactoring(operation.getSuggestedNames());
  }

  protected static void performReplace(@NotNull final GoIntroduceOperation operation) {
    final Project project = operation.getProject();
    final PsiElement expression = operation.getExpression();
    final List<PsiElement> occurrences = operation.isReplaceAll() ? operation.getOccurrences() : Collections.singletonList(expression);
    final PsiElement anchor = GoRefactoringUtil.findLocalAnchor(occurrences);
    if (anchor == null) {
      showCannotPerform(operation, RefactoringBundle.message("refactoring.introduce.context.error"));
      return;
    }
    final GoBlock context = (GoBlock)anchor.getParent();
    final String name = operation.getName();
    final List<PsiElement> newOccurrences = ContainerUtil.newArrayList();
    WriteCommandAction.runWriteCommandAction(project, new Runnable() {
      @Override
      public void run() {
        GoStatement declarationStatement = GoElementFactory.createShortVarDeclarationStatement(project, name, (GoExpression)expression);
        PsiElement newLine = GoElementFactory.createNewLine(project);
        PsiElement statement = context.addBefore(declarationStatement, context.addBefore(newLine, anchor));
        operation.setVar(PsiTreeUtil.findChildOfType(statement, GoVarDefinition.class));

        for (PsiElement occurrence : occurrences) {
          PsiElement occurrenceParent = occurrence.getParent();
          if (occurrenceParent instanceof GoParenthesesExpr) occurrence = occurrenceParent;
          newOccurrences.add(occurrence.replace(GoElementFactory.createReferenceExpression(project, name)));
        }
      }
    });
    operation.setOccurrences(newOccurrences);
    PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(operation.getEditor().getDocument());
  }

  @NotNull
  private static LinkedHashSet<String> getNamesInContext(@Nullable PsiElement context) {
    if (context == null) return ContainerUtil.newLinkedHashSet();
    LinkedHashSet<String> names = ContainerUtil.newLinkedHashSet();

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
    // todo rewrite with names resolve; check occurrences contexts
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

  protected static void showCannotPerform(@NotNull GoIntroduceOperation operation, String message) {
    message = RefactoringBundle.getCannotRefactorMessage(message);
    CommonRefactoringUtil
      .showErrorHint(operation.getProject(), operation.getEditor(), message, RefactoringBundle.getCannotRefactorMessage(null),
                     "refactoring.extractVariable");
  }

  private static class GoInplaceVariableIntroducer extends InplaceVariableIntroducer<PsiElement> {
    public GoInplaceVariableIntroducer(@NotNull GoIntroduceOperation operation) {
      super(operation.getVar(), operation.getEditor(), operation.getProject(), "Introduce Variable",
            ArrayUtil.toObjectArray(operation.getOccurrences(), PsiElement.class), null);
    }
  }
}
