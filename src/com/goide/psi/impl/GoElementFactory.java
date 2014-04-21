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
  public static PsiElement createIdentifierFromText(@NotNull Project project, String text) {
    GoFile file = createFileFromText(project, "package " + text);
    return file.getPackage().getIdentifier();
  }

  @NotNull
  public static GoImportDeclaration createImportDeclaration(@NotNull Project project, @NotNull String importString,
                                                            @Nullable String alias, boolean withParens) {
    importString = StringUtil.isQuotedString(importString) ? importString : StringUtil.wrapWithDoubleQuote(importString);
    alias = alias != null ? alias + " " : "";
    GoFile file = withParens
                  ? createFileFromText(project, "package main\nimport (\n" + alias + importString + "\n)")
                  : createFileFromText(project, "package main\nimport " + alias + importString);
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
  public static GoPackageClause createPackageClause(Project project, String name) {
    return createFileFromText(project, "package " + name).getPackage();
  }
}