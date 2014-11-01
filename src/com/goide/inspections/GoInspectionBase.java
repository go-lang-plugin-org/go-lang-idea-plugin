package com.goide.inspections;

import com.goide.psi.GoFile;
import com.goide.psi.GoVisitor;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class GoInspectionBase extends LocalInspectionTool {
  private static final PsiElementVisitor DUMMY_VISITOR = new PsiElementVisitor() { };

  @NotNull
  @Override
  public final PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
    GoFile file = ObjectUtils.tryCast(session.getFile(), GoFile.class);
    return file != null && canRunOn(file) ? buildGoVisitor(holder, session) : DUMMY_VISITOR;
  }

  @NotNull
  @Override
  public final PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    throw new IllegalStateException();
  }

  @Nullable
  @Override
  public final ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
    throw new IllegalStateException();
  }

  protected boolean canRunOn(@SuppressWarnings({"UnusedParameters", "For future"}) @NotNull GoFile file) {
    return true;
  }

  @NotNull
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @SuppressWarnings({"UnusedParameters", "For future"}) @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitFile(PsiFile file) {
        checkFile((GoFile)file, holder);
      }
    };
  }

  protected void checkFile(@NotNull GoFile file, @NotNull ProblemsHolder problemsHolder) {
  }
}
