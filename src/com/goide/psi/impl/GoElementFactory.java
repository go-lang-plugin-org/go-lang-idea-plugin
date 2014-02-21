package com.goide.psi.impl;

import com.goide.GoLanguage;
import com.goide.psi.GoFile;
import com.goide.psi.GoImportString;
import com.goide.psi.GoReferenceExpression;
import com.goide.psi.GoTypeReferenceExpression;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ConstantConditions")
public class GoElementFactory {
  private GoElementFactory() {
  }

  @NotNull
  public static GoReferenceExpression createReferenceFromText(@NotNull Project project, @NotNull String text) {
    GoFile file = createFileFromText(project, "package f; func f() { " + text + "}");
    return PsiTreeUtil.findChildOfType(file.getFunctions().get(0).getBlock(), GoReferenceExpression.class);
  }

  @NotNull
  private static GoFile createFileFromText(@NotNull Project project, @NotNull String text) {
    return (GoFile)PsiFileFactory.getInstance(project).createFileFromText("a.go", GoLanguage.INSTANCE, text);
  }

  @NotNull
  public static GoTypeReferenceExpression createTypeReferenceFromText(@NotNull Project project, String text) {
    GoFile file = createFileFromText(project, "package f; var oo " + text + ";");
    return PsiTreeUtil.findChildOfType(file, GoTypeReferenceExpression.class);
  }

  @NotNull
  public static PsiElement createIdentifierFromText(@NotNull Project project, String text) {
    GoFile file = createFileFromText(project, "package " + text);
    return file.getPackage().getIdentifier();
  }

  @NotNull
  public static GoImportString createImportString(@NotNull Project project, @NotNull String importString) {
    GoFile file = createFileFromText(project, "package main\nimport " + importString);
    return PsiTreeUtil.findChildOfType(file, GoImportString.class);
  }
}
