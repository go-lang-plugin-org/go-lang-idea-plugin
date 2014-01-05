package com.goide.psi.impl;

import com.goide.GoLanguage;
import com.goide.psi.GoFile;
import com.goide.psi.GoReferenceExpression;
import com.goide.psi.GoVarDefinition;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ConstantConditions")
public class GoElementFactory {
  private GoElementFactory() {
  }

  @NotNull
  public static GoReferenceExpression createReferenceFromText(@NotNull Project project, @NotNull String text) {
    GoFile file = createFileFromText(project, "package f; func f() { " + text + "}");
    return (GoReferenceExpression)file.getFunctions().get(0).getBlock().getStatementList().get(0).getFirstChild();
  }

  @NotNull
  public static GoVarDefinition createVarDefinitionFromText(@NotNull Project project, @NotNull String text) {
    GoFile file = createFileFromText(project, "package f; var "+text+" int;");
    return file.getVars().get(0);
  }

  @NotNull
  public static PsiElement createLeafFromText(@NotNull Project project, @NotNull String text) {
    return createFileFromText(project, text).getFirstChild();
  }

  @NotNull
  private static GoFile createFileFromText(@NotNull Project project, @NotNull String text) {
    return (GoFile) PsiFileFactory.getInstance(project).createFileFromText("a.go", GoLanguage.INSTANCE, text);
  }
}
