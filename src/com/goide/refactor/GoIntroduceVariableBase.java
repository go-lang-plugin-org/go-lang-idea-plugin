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

package com.goide.refactor;

import com.goide.inspections.GoInspectionUtil;
import com.goide.psi.*;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.Pass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SyntaxTraverser;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.introduce.inplace.InplaceVariableIntroducer;
import com.intellij.refactoring.introduce.inplace.OccurrencesChooser;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class GoIntroduceVariableBase {
  protected static void performAction(GoIntroduceOperation operation) {
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
    List<GoExpression> extractableExpressions = collectExtractableExpressions(expression);
    if (extractableExpressions.isEmpty()) {
      showCannotPerform(operation, RefactoringBundle.message("refactoring.introduce.context.error"));
      return;
    }
    List<GoExpression> expressions = ContainerUtil.filter(extractableExpressions,
                                                          expression12 -> GoInspectionUtil.getExpressionResultCount(expression12) == 1);
    if (expressions.isEmpty()) {
      GoExpression closestExpression = ContainerUtil.getFirstItem(extractableExpressions);
      int resultCount = closestExpression != null ? GoInspectionUtil.getExpressionResultCount(closestExpression) : 0;
      showCannotPerform(operation, "Expression " + (closestExpression != null ? closestExpression.getText() + " " : "") +
                                   (resultCount == 0 ? "doesn't return a value." : "returns multiple values."));
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
        public void pass(GoExpression expression) {
          if (expression.isValid()) {
            operation.setExpression(expression);
            performOnElement(operation);
          }
        }
      }, expression1 -> expression1.isValid() ? expression1.getText() : "<invalid expression>");
    }
  }

  @Nullable
  public static GoExpression findExpressionInSelection(@NotNull PsiFile file, int start, int end) {
    return PsiTreeUtil.findElementOfClassAtRange(file, start, end, GoExpression.class);
  }

  @Nullable
  private static GoExpression findExpressionAtOffset(GoIntroduceOperation operation) {
    PsiFile file = operation.getFile();
    int offset = operation.getEditor().getCaretModel().getOffset();

    GoExpression expr = PsiTreeUtil.getNonStrictParentOfType(file.findElementAt(offset), GoExpression.class);
    GoExpression preExpr = PsiTreeUtil.getNonStrictParentOfType(file.findElementAt(offset - 1), GoExpression.class);
    if (expr == null || preExpr != null && PsiTreeUtil.isAncestor(expr, preExpr, false)) return preExpr;
    return expr;
  }

  @NotNull
  private static List<GoExpression> collectExtractableExpressions(@NotNull GoExpression expression) {
    if (PsiTreeUtil.getParentOfType(expression, GoStatement.class) == null) {
      return Collections.emptyList();
    }
    return SyntaxTraverser.psiApi().parents(expression).takeWhile(Conditions.notInstanceOf(GoTopLevelDeclaration.class))
      .filter(GoExpression.class)
      .filter(Conditions.notInstanceOf(GoParenthesesExpr.class))
      .filter(expression1 -> !(expression1 instanceof GoReferenceExpression && expression1.getParent() instanceof GoCallExpr))
      .toList();
  }

  private static void performOnElement(@NotNull GoIntroduceOperation operation) {
    GoExpression expression = operation.getExpression();
    LinkedHashSet<String> suggestedNames = GoRefactoringUtil.getSuggestedNames(expression);
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

  private static void performInplaceIntroduce(GoIntroduceOperation operation) {
    if (!operation.getExpression().isValid()) {
      showCannotPerform(operation, RefactoringBundle.message("refactoring.introduce.context.error"));
      return;
    }
    performReplace(operation);
    new GoInplaceVariableIntroducer(operation).performInplaceRefactoring(operation.getSuggestedNames());
  }

  private static void performReplace(GoIntroduceOperation operation) {
    Project project = operation.getProject();
    PsiElement expression = operation.getExpression();
    List<PsiElement> occurrences = operation.isReplaceAll() ? operation.getOccurrences() : Collections.singletonList(expression);
    PsiElement anchor = GoRefactoringUtil.findLocalAnchor(occurrences);
    if (anchor == null) {
      showCannotPerform(operation, RefactoringBundle.message("refactoring.introduce.context.error"));
      return;
    }
    GoBlock context = (GoBlock)anchor.getParent();
    String name = operation.getName();
    List<PsiElement> newOccurrences = ContainerUtil.newArrayList();
    WriteCommandAction.runWriteCommandAction(project, () -> {
      GoStatement declarationStatement = GoElementFactory.createShortVarDeclarationStatement(project, name, (GoExpression)expression);
      PsiElement newLine = GoElementFactory.createNewLine(project);
      PsiElement statement = context.addBefore(declarationStatement, context.addBefore(newLine, anchor));
      GoVarDefinition varDefinition = PsiTreeUtil.findChildOfType(statement, GoVarDefinition.class);
      assert varDefinition != null;
      assert varDefinition.isValid() : "invalid var `" + varDefinition.getText() + "` definition in `" + statement.getText() + "`";
      operation.setVar(varDefinition);

      boolean firstOccurrence = true;
      for (PsiElement occurrence : occurrences) {
        PsiElement occurrenceParent = occurrence.getParent();
        if (occurrenceParent instanceof GoParenthesesExpr) occurrence = occurrenceParent;

        if (firstOccurrence && occurrence instanceof GoExpression) {
          firstOccurrence = false;
          PsiElement parent = occurrence.getParent();
          // single-expression statement
          if (parent instanceof GoLeftHandExprList && parent.getParent() instanceof GoSimpleStatement) {
            parent.getParent().delete();
            continue;
          }
        }
        newOccurrences.add(occurrence.replace(GoElementFactory.createReferenceExpression(project, name)));
      }
      operation.getEditor().getCaretModel().moveToOffset(varDefinition.getIdentifier().getTextRange().getStartOffset());
    });
    operation.setOccurrences(newOccurrences);
    PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(operation.getEditor().getDocument());
  }

  private static void showCannotPerform(GoIntroduceOperation operation, String message) {
    message = RefactoringBundle.getCannotRefactorMessage(message);
    CommonRefactoringUtil.showErrorHint(operation.getProject(), operation.getEditor(), message, 
                                        RefactoringBundle.getCannotRefactorMessage(null), "refactoring.extractVariable");
  }

  private static class GoInplaceVariableIntroducer extends InplaceVariableIntroducer<PsiElement> {
    public GoInplaceVariableIntroducer(GoIntroduceOperation operation) {
      super(operation.getVar(), operation.getEditor(), operation.getProject(), "Introduce Variable",
            ArrayUtil.toObjectArray(operation.getOccurrences(), PsiElement.class), null);
    }
  }
}
