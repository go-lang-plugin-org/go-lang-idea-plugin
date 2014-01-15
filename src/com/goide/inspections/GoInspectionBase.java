package com.goide.inspections;

import com.goide.GoLanguage;
import com.goide.inspections.suppresion.GoSuppressInspectionFix;
import com.goide.psi.GoCompositeElement;
import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoStatement;
import com.intellij.codeInspection.*;
import com.intellij.lang.Commenter;
import com.intellij.lang.LanguageCommenters;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class GoInspectionBase extends LocalInspectionTool implements BatchSuppressableTool {
  private static Pattern SUPPRESS_PATTERN = Pattern.compile(SuppressionUtil.COMMON_SUPPRESS_REGEXP);

  @Override
  public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
    ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);
    try {
      checkFile(file, problemsHolder);
    }
    catch (PsiInvalidElementAccessException ignored) {
    }
    return problemsHolder.getResultsArray();
  }

  protected abstract void checkFile(PsiFile file, ProblemsHolder problemsHolder);

  @NotNull
  @Override
  public SuppressQuickFix[] getBatchSuppressActions(@Nullable PsiElement element) {
    return new SuppressQuickFix[]{
      new GoSuppressInspectionFix("Suppress all inspections for function", GoFunctionDeclaration.class),
      new GoSuppressInspectionFix(getShortName(), "Suppress for function", GoFunctionDeclaration.class),
      new GoSuppressInspectionFix("Suppress all inspections for statement", GoStatement.class),
      new GoSuppressInspectionFix(getShortName(), "Suppress for statement", GoStatement.class)
    };
  }

  @Override
  public boolean isSuppressedFor(@NotNull PsiElement element) {
    return isSuppressedForParent(element, GoFunctionDeclaration.class) || isSuppressedForParent(element, GoStatement.class);
  }

  private boolean isSuppressedForParent(@Nullable PsiElement element, Class<? extends GoCompositeElement> parentClass) {
    PsiElement parent = PsiTreeUtil.getParentOfType(element, parentClass, false);
    if (parent == null) return false;
    return isSuppressedForElement(parent);
  }

  private boolean isSuppressedForElement(@Nullable PsiElement element) {
    if (element == null) return false;
    Commenter commenter = LanguageCommenters.INSTANCE.forLanguage(GoLanguage.INSTANCE);
    String prefix = ObjectUtils.notNull(commenter == null ? null : commenter.getLineCommentPrefix(), "");

    PsiElement prevSibling = element.getPrevSibling();
    if (prevSibling == null) {
      PsiElement parent = element.getParent();
      if (parent != null) {
        prevSibling = parent.getPrevSibling();
      }
    }
    while (prevSibling instanceof PsiComment || prevSibling instanceof PsiWhiteSpace) {
      if (prevSibling instanceof PsiComment) {
        int prefixLength = prefix.length();
        String text = prevSibling.getText();
        if (text.length() >= prefixLength && isSuppressedInComment(text.substring(prefixLength).trim())) {
          return true;
        }
      }
      prevSibling = prevSibling.getPrevSibling();
    }
    return false;
  }

  private boolean isSuppressedInComment(String commentText) {
    Matcher m = SUPPRESS_PATTERN.matcher(commentText);
    return m.matches() && SuppressionUtil.isInspectionToolIdMentioned(m.group(1), getShortName());
  }
}
