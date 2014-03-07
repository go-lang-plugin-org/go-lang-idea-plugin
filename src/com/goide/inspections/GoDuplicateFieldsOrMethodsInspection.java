package com.goide.inspections;

import com.goide.psi.*;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING;

public class GoDuplicateFieldsOrMethodsInspection extends GoInspectionBase {
  @Override
  protected void checkFile(PsiFile file, final ProblemsHolder problemsHolder) {
    if (!(file instanceof GoFile)) return;
    file.accept(new GoRecursiveVisitor() {
      @Override
      public void visitStructType(@NotNull final GoStructType type) {
        final List<GoNamedElement> fields = ContainerUtil.newArrayList();
        type.accept(new GoRecursiveVisitor() {
          @Override
          public void visitFieldDefinition(@NotNull GoFieldDefinition o) {
            fields.add(o);
          }

          @Override
          public void visitAnonymousFieldDefinition(@NotNull GoAnonymousFieldDefinition o) {
            fields.add(o);
          }

          @Override
          public void visitType(@NotNull GoType o) {
            if (o == type) super.visitType(o); 
          }
        });
        check(fields, problemsHolder, "field");
        super.visitStructType(type);
      }

      @Override
      public void visitInterfaceType(@NotNull GoInterfaceType o) {
        check(o.getMethodSpecList(), problemsHolder, "method");
        super.visitInterfaceType(o);
      }
    });
  }

  private static void check(@NotNull List<? extends GoNamedElement> fields, @NotNull ProblemsHolder problemsHolder, @NotNull String what) {
    Set<String> names = ContainerUtil.newHashSet();
    for (GoCompositeElement field : fields) {
      if (field instanceof GoNamedElement) {
        String name = ((GoNamedElement)field).getName();
        if (names.contains(name)) {
          PsiElement id = ((GoNamedElement)field).getIdentifier();
          problemsHolder.registerProblem(id != null ? id : field, "Duplicate " + what + " " + "'" + name + "'", GENERIC_ERROR_OR_WARNING);
        }
        else {
          ContainerUtil.addIfNotNull(names, name);
        }
      }
    }
  }
}
