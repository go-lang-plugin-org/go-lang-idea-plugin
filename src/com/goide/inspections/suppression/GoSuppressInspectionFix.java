package com.goide.inspections.suppression;

import com.goide.psi.GoCompositeElement;
import com.intellij.codeInsight.daemon.impl.actions.AbstractBatchSuppressByNoInspectionCommentFix;
import com.intellij.codeInspection.SuppressionUtil;
import com.intellij.codeInspection.SuppressionUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiParserFacade;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoSuppressInspectionFix extends AbstractBatchSuppressByNoInspectionCommentFix {
  private Class<? extends GoCompositeElement> myContainerClass;

  public GoSuppressInspectionFix(String text, Class<? extends GoCompositeElement> containerClass) {
    super(SuppressionUtil.ALL, true);
    setText(text);
    myContainerClass = containerClass;
  }

  public GoSuppressInspectionFix(String ID, String text, Class<? extends GoCompositeElement> containerClass) {
    super(ID, false);
    setText(text);
    myContainerClass = containerClass;
  }

  @Override
  @Nullable
  public PsiElement getContainer(PsiElement context) {
    return PsiTreeUtil.getNonStrictParentOfType(context, myContainerClass);
  }

  //todo: remove after formatting implementation
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
