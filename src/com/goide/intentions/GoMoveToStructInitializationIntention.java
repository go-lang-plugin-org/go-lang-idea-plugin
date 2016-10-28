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

package com.goide.intentions;

import com.goide.psi.*;
import com.goide.psi.impl.GoElementFactory;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static com.intellij.util.containers.ContainerUtil.*;

public class GoMoveToStructInitializationIntention extends BaseElementAtCaretIntentionAction {
  public static final String NAME = "Move field assignment to struct initialization";

  public GoMoveToStructInitializationIntention() {
    setText(NAME);
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return NAME;
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
    return getData(element) != null;
  }

  @Nullable
  private static Data getData(@NotNull PsiElement element) {
    if (!element.isValid() || !element.isWritable()) return null;
    GoAssignmentStatement assignment = getValidAssignmentParent(element);
    GoReferenceExpression selectedFieldReference = assignment != null ? getFieldReferenceExpression(element, assignment) : null;
    GoCompositeLit compositeLit = selectedFieldReference != null ? getStructLiteralByReference(selectedFieldReference, assignment) : null;
    if (compositeLit == null) return null;

    List<GoReferenceExpression> references = getUninitializedSingleFieldReferences(assignment, selectedFieldReference, compositeLit);
    return !references.isEmpty() ? new Data(assignment, compositeLit, references) : null;
  }

  @Nullable
  private static GoAssignmentStatement getValidAssignmentParent(@Nullable PsiElement element) {
    GoAssignmentStatement assignment = PsiTreeUtil.getNonStrictParentOfType(element, GoAssignmentStatement.class);
    return assignment != null && assignment.isValid() && getLeftHandElements(assignment).size() == assignment.getExpressionList().size()
           ? assignment : null;
  }

  @Nullable
  private static GoReferenceExpression getFieldReferenceExpression(@NotNull PsiElement selectedElement,
                                                                   @NotNull GoAssignmentStatement assignment) {
    GoReferenceExpression selectedReferenceExpression = PsiTreeUtil.getTopmostParentOfType(selectedElement, GoReferenceExpression.class);
    if (isFieldReferenceExpression(selectedReferenceExpression)) {
      return !isAssignedInPreviousStatement(selectedReferenceExpression, assignment) ? selectedReferenceExpression : null;
    }

    List<GoReferenceExpression> fieldReferenceExpressions = getFieldReferenceExpressions(assignment);
    if (exists(fieldReferenceExpressions, expression -> isAssignedInPreviousStatement(expression, assignment))) return null;

    Set<PsiElement> resolvedFields = map2Set(fieldReferenceExpressions, GoMoveToStructInitializationIntention::resolveQualifier);
    return resolvedFields.size() == 1 ? getFirstItem(fieldReferenceExpressions) : null;
  }

  @NotNull
  private static List<GoReferenceExpression> getFieldReferenceExpressions(@NotNull GoAssignmentStatement assignment) {
    return filter(map(getLeftHandElements(assignment), GoMoveToStructInitializationIntention::unwrapParensAndCast),
                  GoMoveToStructInitializationIntention::isFieldReferenceExpression);
  }

  @Nullable
  private static GoReferenceExpression unwrapParensAndCast(@Nullable PsiElement e) {
    while (e instanceof GoParenthesesExpr) {
      e = ((GoParenthesesExpr)e).getExpression();
    }
    return ObjectUtils.tryCast(e, GoReferenceExpression.class);
  }

  @Contract("null -> false")
  private static boolean isFieldReferenceExpression(@Nullable PsiElement element) {
    return element instanceof GoReferenceExpression && isFieldDefinition(((GoReferenceExpression)element).resolve());
  }

  @Contract("null -> false")
  private static boolean isFieldDefinition(@Nullable PsiElement element) {
    return element instanceof GoFieldDefinition || element instanceof GoAnonymousFieldDefinition;
  }

  private static boolean isAssignedInPreviousStatement(@NotNull GoExpression referenceExpression,
                                                       @NotNull GoAssignmentStatement assignment) {
    GoReferenceExpression rightExpression =
      unwrapParensAndCast(GoPsiImplUtil.getRightExpression(assignment, getTopmostExpression(referenceExpression)));

    PsiElement resolve = rightExpression != null ? rightExpression.resolve() : null;
    GoStatement previousElement = resolve != null ? PsiTreeUtil.getPrevSiblingOfType(assignment, GoStatement.class) : null;
    return previousElement != null && exists(getLeftHandElements(previousElement), e -> isResolvedTo(e, resolve));
  }

  @NotNull
  private static GoExpression getTopmostExpression(@NotNull GoExpression expression) {
    return ObjectUtils.notNull(PsiTreeUtil.getTopmostParentOfType(expression, GoExpression.class), expression);
  }

  private static boolean isResolvedTo(@Nullable PsiElement e, @Nullable PsiElement resolve) {
    if (e instanceof GoVarDefinition) return resolve == e;

    GoReferenceExpression refExpression = unwrapParensAndCast(e);
    return refExpression != null && refExpression.resolve() == resolve;
  }

  @NotNull
  private static List<GoReferenceExpression> getUninitializedSingleFieldReferences(@NotNull GoAssignmentStatement assignment,
                                                                                   @NotNull GoReferenceExpression fieldReferenceExpression,
                                                                                   @NotNull GoCompositeLit compositeLit) {
    PsiElement resolve = resolveQualifier(fieldReferenceExpression);
    List<GoReferenceExpression> uninitializedFieldReferencesByQualifier =
      filter(getUninitializedFieldReferenceExpressions(assignment, compositeLit), e -> isResolvedTo(e.getQualifier(), resolve));
    MultiMap<PsiElement, GoReferenceExpression> resolved = groupBy(uninitializedFieldReferencesByQualifier, GoReferenceExpression::resolve);
    return map(filter(resolved.entrySet(), set -> set.getValue().size() == 1), set -> getFirstItem(set.getValue()));
  }

  @Nullable
  private static GoCompositeLit getStructLiteralByReference(@NotNull GoReferenceExpression fieldReferenceExpression,
                                                            @NotNull GoAssignmentStatement assignment) {
    GoStatement previousStatement = PsiTreeUtil.getPrevSiblingOfType(assignment, GoStatement.class);
    if (previousStatement instanceof GoSimpleStatement) {
      return getStructLiteral(fieldReferenceExpression, (GoSimpleStatement)previousStatement);
    }
    if (previousStatement instanceof GoAssignmentStatement) {
      return getStructLiteral(fieldReferenceExpression, (GoAssignmentStatement)previousStatement);
    }
    return null;
  }

  @Nullable
  private static GoCompositeLit getStructLiteral(@NotNull GoReferenceExpression fieldReferenceExpression,
                                                 @NotNull GoSimpleStatement structDeclaration) {
    GoShortVarDeclaration varDeclaration = structDeclaration.getShortVarDeclaration();
    if (varDeclaration == null) return null;

    PsiElement resolve = resolveQualifier(fieldReferenceExpression);
    GoVarDefinition structVarDefinition = find(varDeclaration.getVarDefinitionList(), definition -> resolve == definition);
    return structVarDefinition != null ? ObjectUtils.tryCast(structVarDefinition.getValue(), GoCompositeLit.class) : null;
  }

  @Nullable
  private static PsiElement resolveQualifier(@NotNull GoReferenceExpression fieldReferenceExpression) {
    GoReferenceExpression qualifier = fieldReferenceExpression.getQualifier();
    return qualifier != null ? qualifier.resolve() : null;
  }

  @Nullable
  private static GoCompositeLit getStructLiteral(@NotNull GoReferenceExpression fieldReferenceExpression,
                                                 @NotNull GoAssignmentStatement structAssignment) {
    GoVarDefinition varDefinition = ObjectUtils.tryCast(resolveQualifier(fieldReferenceExpression), GoVarDefinition.class);
    PsiElement field = fieldReferenceExpression.resolve();
    if (varDefinition == null || !isFieldDefinition(field) || !hasStructTypeWithField(varDefinition, (GoNamedElement)field)) {
      return null;
    }

    GoExpression structReferenceExpression = find(structAssignment.getLeftHandExprList().getExpressionList(),
                                                  expression -> isResolvedTo(expression, varDefinition));
    if (structReferenceExpression == null) return null;
    GoExpression compositeLit = GoPsiImplUtil.getRightExpression(structAssignment, structReferenceExpression);
    return ObjectUtils.tryCast(compositeLit, GoCompositeLit.class);
  }

  private static boolean hasStructTypeWithField(@NotNull GoVarDefinition structVarDefinition, @NotNull GoNamedElement field) {
    GoType type = structVarDefinition.getGoType(null);
    GoStructType structType = type != null ? ObjectUtils.tryCast(type.getUnderlyingType(), GoStructType.class) : null;
    return structType != null && PsiTreeUtil.isAncestor(structType, field, true);
  }

  private static boolean isFieldInitialization(@NotNull GoElement element, @NotNull PsiElement field) {
    GoKey key = element.getKey();
    GoFieldName fieldName = key != null ? key.getFieldName() : null;
    return fieldName != null && fieldName.resolve() == field;
  }

  @NotNull
  private static List<GoReferenceExpression> getUninitializedFieldReferenceExpressions(@NotNull GoAssignmentStatement assignment,
                                                                                       @NotNull GoCompositeLit structLiteral) {
    return filter(getFieldReferenceExpressions(assignment), expression ->
      isUninitializedFieldReferenceExpression(expression, structLiteral) && !isAssignedInPreviousStatement(expression, assignment));
  }

  @Contract("null, _-> false")
  private static boolean isUninitializedFieldReferenceExpression(@Nullable GoReferenceExpression fieldReferenceExpression,
                                                                 @NotNull GoCompositeLit structLiteral) {
    if (fieldReferenceExpression == null) return false;
    GoLiteralValue literalValue = structLiteral.getLiteralValue();
    PsiElement resolve = fieldReferenceExpression.resolve();
    return literalValue != null && isFieldDefinition(resolve) &&
           !exists(literalValue.getElementList(), element -> isFieldInitialization(element, resolve));
  }

  @NotNull
  private static List<? extends PsiElement> getLeftHandElements(@NotNull GoStatement statement) {
    if (statement instanceof GoSimpleStatement) {
      GoShortVarDeclaration varDeclaration = ((GoSimpleStatement)statement).getShortVarDeclaration();
      return varDeclaration != null ? varDeclaration.getVarDefinitionList() : emptyList();
    }
    if (statement instanceof GoAssignmentStatement) {
      return ((GoAssignmentStatement)statement).getLeftHandExprList().getExpressionList();
    }
    return emptyList();
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
    Data data = getData(element);
    if (data == null) return;
    moveFieldReferenceExpressions(data);
  }

  private static void moveFieldReferenceExpressions(@NotNull Data data) {
    GoLiteralValue literalValue = data.getCompositeLit().getLiteralValue();
    if (literalValue == null) return;

    for (GoReferenceExpression expression : data.getReferenceExpressions()) {
      GoExpression anchor = getTopmostExpression(expression);
      GoExpression fieldValue = GoPsiImplUtil.getRightExpression(data.getAssignment(), anchor);
      if (fieldValue == null) continue;

      GoPsiImplUtil.deleteExpressionFromAssignment(data.getAssignment(), anchor);
      addFieldDefinition(literalValue, expression.getIdentifier().getText(), fieldValue.getText());
    }
  }

  private static void addFieldDefinition(@NotNull GoLiteralValue literalValue, @NotNull String name, @NotNull String value) {
    Project project = literalValue.getProject();
    PsiElement newField = GoElementFactory.createLiteralValueElement(project, name, value);
    GoElement lastElement = getLastItem(literalValue.getElementList());
    if (lastElement == null) {
      literalValue.addAfter(newField, literalValue.getLbrace());
    }
    else {
      lastElement.add(GoElementFactory.createComma(project));
      lastElement.add(newField);
    }
  }

  private static class Data {
    private final GoCompositeLit myCompositeLit;
    private final GoAssignmentStatement myAssignment;
    private final List<GoReferenceExpression> myReferenceExpressions;

    public Data(@NotNull GoAssignmentStatement assignment,
                @NotNull GoCompositeLit compositeLit,
                @NotNull List<GoReferenceExpression> referenceExpressions) {
      myCompositeLit = compositeLit;
      myAssignment = assignment;
      myReferenceExpressions = referenceExpressions;
    }

    public GoCompositeLit getCompositeLit() {
      return myCompositeLit;
    }

    public GoAssignmentStatement getAssignment() {
      return myAssignment;
    }

    public List<GoReferenceExpression> getReferenceExpressions() {
      return myReferenceExpressions;
    }
  }
}
