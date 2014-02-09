package com.goide.inspections;

import com.goide.psi.*;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING;

public class GoDuplicateFieldsInspection extends GoInspectionBase {
  @Override
  protected void checkFile(PsiFile file, final ProblemsHolder problemsHolder) {
    if (!(file instanceof GoFile)) return;
    file.accept(new GoRecursiveVisitor() {
      @Override
      public void visitStructType(@NotNull GoStructType o) {
        final List<GoNamedElement> fields = ContainerUtil.newArrayList();
        o.accept(new GoRecursiveVisitor() {
          @Override
          public void visitFieldDefinition(@NotNull GoFieldDefinition o) {
            fields.add(o);
          }

          @Override
          public void visitAnonymousFieldDefinition(@NotNull GoAnonymousFieldDefinition o) {
            fields.add(o);
          }
        });
        Set<String> names = ContainerUtil.newHashSet();
        for (GoCompositeElement field : fields) {
          if (field instanceof GoNamedElement) {
            String name = ((GoNamedElement)field).getName();
            if (names.contains(name)) {
              problemsHolder.registerProblem(field, "Duplicate field " + "'" + name + "'", GENERIC_ERROR_OR_WARNING);
            }
            else {
              names.add(name);
            }
          }
        }
        super.visitStructType(o);
      }
    });
  }
}
