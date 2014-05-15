package com.goide.inspections;

import com.goide.psi.GoFile;
import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoRecursiveVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class GoDuplicateFunctionInspection extends GoInspectionBase {
  @Override
  protected void checkFile(PsiFile file, final ProblemsHolder problemsHolder) {
    if (!(file instanceof GoFile)) return;

    List<GoFunctionDeclaration> functions = ((GoFile)file).getFunctions();
    final MultiMap<String, GoFunctionDeclaration> map = new MultiMap<String, GoFunctionDeclaration>();
    for (GoFunctionDeclaration function : functions) {
      map.putValue(function.getName(), function);
    }

    file.accept(new GoRecursiveVisitor() {
      @Override
      public void visitFunctionDeclaration(@NotNull GoFunctionDeclaration o) {
        String name = o.getName();
        if (name == null) return;
        Collection<GoFunctionDeclaration> byKey = map.get(name);
        if (byKey.size() > 1) {
          if (o.equals(byKey.iterator().next())) return;
          PsiElement identifier = o.getNameIdentifier();
          problemsHolder.registerProblem(identifier == null ? o : identifier, "Duplicate function name");
        }
      }
    });
  }
}
