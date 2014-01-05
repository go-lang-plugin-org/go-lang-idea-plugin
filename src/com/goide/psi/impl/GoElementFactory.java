package com.goide.psi.impl;

import com.goide.GoLanguage;
import com.goide.psi.GoFile;
import com.goide.psi.GoParamDefinition;
import com.goide.psi.GoReferenceExpression;
import com.goide.psi.GoVarDefinition;
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
  public static GoVarDefinition createVarDefinitionFromText(@NotNull Project project, @NotNull String text) {
    GoFile file = createFileFromText(project, "package f; var " + text + " int;");
    return file.getVars().get(0);
  }

  @NotNull
  public static PsiElement createLeafFromText(@NotNull Project project, @NotNull String text) {
    return createFileFromText(project, text).getFirstChild();
  }

  @NotNull
  private static GoFile createFileFromText(@NotNull Project project, @NotNull String text) {
    return (GoFile)PsiFileFactory.getInstance(project).createFileFromText("a.go", GoLanguage.INSTANCE, text);
  }

  @NotNull
  public static GoParamDefinition createParamDefinitionFromText(Project project, String text) {
    GoFile file = createFileFromText(project, "package f; func f(" + text + " int) { }");
    return PsiTreeUtil.findChildOfType(file.getFunctions().get(0), GoParamDefinition.class);
  }
}
