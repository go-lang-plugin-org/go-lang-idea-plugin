package com.goide.inspections.unresolved;

import com.goide.psi.GoFile;
import com.goide.psi.GoTopLevelDeclaration;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GoUnresolvedFixBase extends LocalQuickFixAndIntentionActionOnPsiElement {
  protected final String myName;
  protected final String myWhat;

  public GoUnresolvedFixBase(@NotNull PsiElement element, @NotNull String name, @NotNull String what) {
    super(element);
    myName = name;
    myWhat = what;
  }

  @Nullable
  protected static GoTopLevelDeclaration getTopLevelDeclaration(PsiElement startElement) {
    GoTopLevelDeclaration decl = PsiTreeUtil.getTopmostParentOfType(startElement, GoTopLevelDeclaration.class);
    if (decl == null || !(decl.getParent() instanceof GoFile)) return null;
    return decl;
  }

  @NotNull
  @Override
  public String getText() {
    return "Create " + myWhat + " '" + myName + "'";
  }

  @NotNull
  @Override
  public String getFamilyName() {
    return "Go";
  }
}
