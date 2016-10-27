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

package com.goide.psi.impl;

import com.goide.GoLanguage;
import com.goide.psi.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.PsiParserFacadeImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("ConstantConditions")
public class GoElementFactory {
  private GoElementFactory() {
  }

  @NotNull
  private static GoFile createFileFromText(@NotNull Project project, @NotNull String text) {
    return (GoFile)PsiFileFactory.getInstance(project).createFileFromText("a.go", GoLanguage.INSTANCE, text);
  }

  @NotNull
  public static PsiElement createReturnStatement(@NotNull Project project) {
    GoFile file = createFileFromText(project, "package main\nfunc _() { return; }");
    return PsiTreeUtil.findChildOfType(file, GoReturnStatement.class);
  }

  @NotNull
  public static PsiElement createSelectStatement(@NotNull Project project) {
    GoFile file = createFileFromText(project, "package main\nfunc _() { select {\n} }");
    return PsiTreeUtil.findChildOfType(file, GoSelectStatement.class);
  }

  @NotNull
  public static PsiElement createIdentifierFromText(@NotNull Project project, String text) {
    GoFile file = createFileFromText(project, "package " + text);
    return file.getPackage().getIdentifier();
  }

  @NotNull
  public static GoIfStatement createIfStatement(@NotNull Project project,
                                                @NotNull String condition,
                                                @NotNull String thenBranch,
                                                @Nullable String elseBranch) {
    String elseText = elseBranch != null ? " else {\n" + elseBranch + "\n}" : "";
    GoFile file = createFileFromText(project, "package a; func _() {\n" +
                                              "if " + condition + " {\n" +
                                              thenBranch + "\n" +
                                              "}" + elseText + "\n" +
                                              "}");
    return PsiTreeUtil.findChildOfType(file, GoIfStatement.class);
  }

  @NotNull
  public static GoImportDeclaration createEmptyImportDeclaration(@NotNull Project project) {
    return PsiTreeUtil.findChildOfType(createFileFromText(project, "package main\nimport (\n\n)"), GoImportDeclaration.class);
  }
                                                            
  @NotNull
  public static GoImportDeclaration createImportDeclaration(@NotNull Project project, @NotNull String importString,
                                                            @Nullable String alias, boolean withParens) {
    importString = GoPsiImplUtil.isQuotedImportString(importString) ? importString : StringUtil.wrapWithDoubleQuote(importString);
    alias = alias != null ? alias + " " : "";
    GoFile file = createFileFromText(project, withParens
                                              ? "package main\nimport (\n" + alias + importString + "\n)"
                                              : "package main\nimport " + alias + importString);
    return PsiTreeUtil.findChildOfType(file, GoImportDeclaration.class);
  }

  @NotNull
  public static GoImportSpec createImportSpec(@NotNull Project project, @NotNull String importString, @Nullable String alias) {
    GoImportDeclaration importDeclaration = createImportDeclaration(project, importString, alias, true);
    return ContainerUtil.getFirstItem(importDeclaration.getImportSpecList());
  }

  @NotNull
  public static GoImportString createImportString(@NotNull Project project, @NotNull String importString) {
    GoImportSpec importSpec = createImportSpec(project, importString, null);
    return importSpec.getImportString();
  }

  @NotNull
  public static PsiElement createNewLine(@NotNull Project project) {
    return PsiParserFacadeImpl.SERVICE.getInstance(project).createWhiteSpaceFromText("\n");
  }

  @NotNull
  public static PsiElement createComma(@NotNull Project project) {
    return createFileFromText(project, "package foo; var a,b = 1,2").getVars().get(0).getNextSibling();
  }

  @NotNull
  public static GoPackageClause createPackageClause(@NotNull Project project, @NotNull String name) {
    return createFileFromText(project, "package " + name).getPackage();
  }

  @NotNull
  public static GoBlock createBlock(@NotNull Project project) {
    GoFunctionDeclaration function = ContainerUtil.getFirstItem(createFileFromText(project, "package a; func t() {\n}").getFunctions());
    assert function != null : "Impossible situation! Parser is broken.";
    return function.getBlock();
  }

  @NotNull
  public static GoStringLiteral createStringLiteral(@NotNull Project project, @NotNull String stringLiteral) {
    GoFile f = createFileFromText(project, "package a; var b = " + stringLiteral);
    return PsiTreeUtil.getNextSiblingOfType(ContainerUtil.getFirstItem(f.getVars()), GoStringLiteral.class);
  }

  @NotNull
  public static GoSignature createFunctionSignatureFromText(@NotNull Project project, @NotNull String text) {
    GoFile file = createFileFromText(project, "package a; func t(" + text + ") {\n}");
    return ContainerUtil.getFirstItem(file.getFunctions()).getSignature();
  }

  @NotNull
  public static GoStatement createShortVarDeclarationStatement(@NotNull Project project,
                                                               @NotNull String name,
                                                               @NotNull GoExpression initializer) {
    GoFile file = createFileFromText(project, "package a; func a() {\n " + name + " := " + initializer.getText() + "}");
    return PsiTreeUtil.findChildOfType(file, GoSimpleStatement.class);
  }

  @NotNull
  public static GoStatement createShortVarDeclarationStatement(@NotNull Project project,
                                                               @NotNull String leftSide,
                                                               @NotNull String rightSide) {
    GoFile file = createFileFromText(project, "package a; func a() {\n " + leftSide + " := " + rightSide + "}");
    return PsiTreeUtil.findChildOfType(file, GoSimpleStatement.class);
  }

  @NotNull
  public static GoRangeClause createRangeClause(@NotNull Project project, @NotNull String leftSide, @NotNull String rightSide) {
    GoFile file = createFileFromText(project, "package a; func a() {\n for " + leftSide + " := range " + rightSide + "{\n}\n}");
    return PsiTreeUtil.findChildOfType(file, GoRangeClause.class);
  }

  @NotNull
  public static GoRangeClause createRangeClauseAssignment(@NotNull Project project, @NotNull String leftSide, @NotNull String rightSide) {
    GoFile file = createFileFromText(project, "package a; func a() {\n for " + leftSide + " = range " + rightSide + "{\n}\n}");
    return PsiTreeUtil.findChildOfType(file, GoRangeClause.class);
  }

  @NotNull
  public static GoRecvStatement createRecvStatement(@NotNull Project project, @NotNull String leftSide, @NotNull String rightSide) {
    GoFile file = createFileFromText(project, "package a; func a() {\n select { case " + leftSide + " := " + rightSide + ":\n}\n}");
    return PsiTreeUtil.findChildOfType(file, GoRecvStatement.class);
  }

  @NotNull
  public static GoRecvStatement createRecvStatementAssignment(@NotNull Project project, @NotNull String left, @NotNull String right) {
    GoFile file = createFileFromText(project, "package a; func a() {\n select { case " + left + " = " + right + ":\n}\n}");
    return PsiTreeUtil.findChildOfType(file, GoRecvStatement.class);
  }

  public static GoAssignmentStatement createAssignmentStatement(@NotNull Project project, @NotNull String left, @NotNull String right) {
    GoFile file = createFileFromText(project, "package a; func a() {\n " + left + " = " + right + "}");
    return PsiTreeUtil.findChildOfType(file, GoAssignmentStatement.class);
  }

  @NotNull
  public static GoDeferStatement createDeferStatement(@NotNull Project project, @NotNull String expressionText) {
    GoFile file = createFileFromText(project, "package a; func a() {\n  defer " + expressionText + "}");
    return PsiTreeUtil.findChildOfType(file, GoDeferStatement.class);
  }

  @NotNull
  public static GoGoStatement createGoStatement(@NotNull Project project, @NotNull String expressionText) {
    GoFile file = createFileFromText(project, "package a; func a() {\n  go " + expressionText + "}");
    return PsiTreeUtil.findChildOfType(file, GoGoStatement.class);
  }

  @NotNull
  public static GoForStatement createForStatement(@NotNull Project project, @NotNull String text) {
    GoFile file = createFileFromText(project, "package a; func a() {\n for {\n" + text +  "\n}\n}");
    return PsiTreeUtil.findChildOfType(file, GoForStatement.class);
  }

  @NotNull
  public static GoExpression createExpression(@NotNull Project project, @NotNull String text) {
    GoFile file = createFileFromText(project, "package a; func a() {\n a := " + text + "}");
    return PsiTreeUtil.findChildOfType(file, GoExpression.class);
  }

  @NotNull
  public static GoReferenceExpression createReferenceExpression(@NotNull Project project, @NotNull String name) {
    GoFile file = createFileFromText(project, "package a; var a = " + name);
    return PsiTreeUtil.findChildOfType(file, GoReferenceExpression.class);
  }

  @NotNull
  public static GoSimpleStatement createComparison(@NotNull Project project, @NotNull String text) {
    GoFile file = createFileFromText(project, "package a; func a() {\n " + text + "}");
    return PsiTreeUtil.findChildOfType(file, GoSimpleStatement.class);
  }

  @NotNull
  public static GoConstDeclaration createConstDeclaration(@NotNull Project project, @NotNull String text) {
    GoFile file = createFileFromText(project, "package a; const " + text);
    return PsiTreeUtil.findChildOfType(file, GoConstDeclaration.class);
  }

  @NotNull
  public static GoConstSpec createConstSpec(@NotNull Project project, @NotNull String name, @Nullable String type, @Nullable String value) {
    GoConstDeclaration constDeclaration = createConstDeclaration(project, prepareVarOrConstDeclarationText(name, type, value));
    return ContainerUtil.getFirstItem(constDeclaration.getConstSpecList());
  }

  @NotNull
  public static GoVarDeclaration createVarDeclaration(@NotNull Project project, @NotNull String text) {
    GoFile file = createFileFromText(project, "package a; var " + text);
    return PsiTreeUtil.findChildOfType(file, GoVarDeclaration.class);
  }

  @NotNull
  public static GoVarSpec createVarSpec(@NotNull Project project, @NotNull String name, @Nullable String type, @Nullable String value) {
    GoVarDeclaration varDeclaration = createVarDeclaration(project, prepareVarOrConstDeclarationText(name, type, value));
    return ContainerUtil.getFirstItem(varDeclaration.getVarSpecList());
  }

  @NotNull
  private static String prepareVarOrConstDeclarationText(@NotNull String name, @Nullable String type, @Nullable String value) {
    type = StringUtil.trim(type);
    value = StringUtil.trim(value);
    type = StringUtil.isNotEmpty(type) ? " " + type : "";
    value = StringUtil.isNotEmpty(value) ? " = " + value : "";
    return name + type + value;
  }

  public static GoTypeList createTypeList(@NotNull Project project, @NotNull String text) {
    GoFile file = createFileFromText(project, "package a; func _() (" + text + "){}");
    return PsiTreeUtil.findChildOfType(file, GoTypeList.class);
  }

  public static GoType createType(@NotNull Project project, @NotNull String text) {
    GoFile file = createFileFromText(project, "package a; var a " + text);
    return PsiTreeUtil.findChildOfType(file, GoType.class);
  }

  public static PsiElement createLiteralValueElement(@NotNull Project project, @NotNull String key, @NotNull String value) {
    GoFile file = createFileFromText(project, "package a; var _ = struct { a string } { " + key + ": " + value + " }");
    return PsiTreeUtil.findChildOfType(file, GoElement.class);
  }

  @NotNull
  public static GoTypeDeclaration createTypeDeclaration(@NotNull Project project, @NotNull String name, @NotNull GoType type) {
    GoFile file = createFileFromText(project, "package a; type " + name + " " + type.getText());
    return PsiTreeUtil.findChildOfType(file, GoTypeDeclaration.class);
  }
}