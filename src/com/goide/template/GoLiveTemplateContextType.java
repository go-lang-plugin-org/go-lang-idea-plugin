package com.goide.template;

import com.goide.GoLanguage;
import com.goide.highlighting.GoSyntaxHighlighter;
import com.goide.psi.GoBlock;
import com.goide.psi.GoFile;
import com.goide.psi.GoSimpleStatement;
import com.intellij.codeInsight.template.EverywhereContextType;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class GoLiveTemplateContextType extends TemplateContextType {
  protected GoLiveTemplateContextType(@NotNull @NonNls String id, @NotNull String presentableName, @Nullable Class<? extends TemplateContextType> baseContextType) {
    super(id, presentableName, baseContextType);
  }

  public boolean isInContext(@NotNull final PsiFile file, final int offset) {
    if (PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(GoLanguage.INSTANCE)) {
      PsiElement element = getFirstCompositeElement(file.findElementAt(offset));
      return element != null && isInContext(element);
    }

    return false;
  }

  @Nullable
  private static PsiElement getFirstCompositeElement(@Nullable PsiElement at) {
    if (at instanceof PsiComment) return at;
    PsiElement result = at;
    while (result != null && (result instanceof PsiWhiteSpace || result.getChildren().length == 0)) {
      result = result.getParent();
    }
    return result;
  }

  protected abstract boolean isInContext(@NotNull PsiElement element);

  public SyntaxHighlighter createHighlighter() {
    return new GoSyntaxHighlighter();
  }

  public static class GoEverywhereContextType extends GoLiveTemplateContextType {
    protected GoEverywhereContextType() {
      super("GO", "Go", EverywhereContextType.class);
    }
  
    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return !(element instanceof PsiComment);
    }
  }
  
  public static class GoFileContextType extends GoLiveTemplateContextType {
    protected GoFileContextType() {
      super("GO_FILE", "Go file", EverywhereContextType.class);
    }
  
    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return element.getParent() instanceof GoFile;
    }
  }
  
  public static class GoBlockContextType extends GoLiveTemplateContextType {
    protected GoBlockContextType() {
      super("GO_BLOCK", "Go block", EverywhereContextType.class);
    }
  
    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return element instanceof GoSimpleStatement && PsiTreeUtil.getParentOfType(element, GoBlock.class) != null;
    }
  }
}
