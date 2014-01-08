package com.goide.inspections;

import com.goide.GoLanguage;
import com.goide.psi.GoCompositeElement;
import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoStatement;
import com.intellij.codeInsight.daemon.impl.actions.AbstractSuppressByNoInspectionCommentFix;
import com.intellij.codeInspection.*;
import com.intellij.lang.Commenter;
import com.intellij.lang.LanguageCommenters;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class GoInspectionBase extends LocalInspectionTool implements CustomSuppressableInspectionTool {
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

  @Nullable
  @Override
  public SuppressIntentionAction[] getSuppressActions(@Nullable PsiElement element) {
    return new SuppressIntentionAction[]{
      new GoSuppressInspectionFix(getSuppressId(), "Suppress for function", GoFunctionDeclaration.class),
      new GoSuppressInspectionFix(getSuppressId(), "Suppress for statement", GoStatement.class)
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
    return m.matches() && SuppressionUtil.isInspectionToolIdMentioned(m.group(1), getSuppressId());
  }

  private String getSuppressId() {
    return getShortName().replace("Inspection", "");
  }

  public static class GoSuppressInspectionFix extends AbstractSuppressByNoInspectionCommentFix {
    private Class<? extends GoCompositeElement> myContainerClass;

    public GoSuppressInspectionFix(String ID, String text, Class<? extends GoCompositeElement> containerClass) {
      super(ID, false);
      setText(text);
      myContainerClass = containerClass;
    }

    @Override
    @Nullable
    protected PsiElement getContainer(PsiElement context) {
      return PsiTreeUtil.getParentOfType(context, myContainerClass);
    }

    @Override
    protected void createSuppression(@NotNull Project project, @NotNull PsiElement element, @NotNull PsiElement container) throws IncorrectOperationException {
      PsiParserFacade parserFacade = PsiParserFacade.SERVICE.getInstance(project);
      String text = SuppressionUtilCore.SUPPRESS_INSPECTIONS_TAG_NAME + " " + myID;
      PsiComment comment = parserFacade.createLineOrBlockCommentFromText(element.getContainingFile().getLanguage(), text);
      PsiElement where = container.getParent().addBefore(comment, container);
      PsiElement spaceFromText = PsiParserFacade.SERVICE.getInstance(project).createWhiteSpaceFromText("\n");
      where.getParent().addAfter(spaceFromText, where);
    }
  }
}
