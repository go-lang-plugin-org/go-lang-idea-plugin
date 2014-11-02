package com.goide.inspections;

import com.goide.psi.*;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GoDuplicateArgumentInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder,
                                     @SuppressWarnings({"UnusedParameters", "For future"}) @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitSignatureOwner(@NotNull GoSignatureOwner o) {
        check(o.getSignature(), holder);
      }

      @Override
      public void visitFunctionLit(@NotNull GoFunctionLit o) {
        check(o.getSignature(), holder);
      }

      @Override
      public void visitFunctionOrMethodDeclaration(@NotNull GoFunctionOrMethodDeclaration o) {
        check(o.getSignature(), holder);
      }
    };
  }

  public void check(@Nullable GoSignature o, @NotNull ProblemsHolder holder) {
    if (o == null) return;
    List<GoParameterDeclaration> params = o.getParameters().getParameterDeclarationList();
    Set<String> parameters = new LinkedHashSet<String>();
    for (GoParameterDeclaration fp : params) {
      for (GoParamDefinition parameter : fp.getParamDefinitionList()) {
        String text = parameter.getIdentifier().getText();
        if ("_".equals(text)) continue;
        if (parameters.contains(text)) {
          holder.registerProblem(parameter, errorText(text), ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
        }
        else {
          parameters.add(text);
        }
      }
    }
  }

  protected static String errorText(@NotNull String name) {
    return "Duplicate argument " + "'" + name + "'";
  }
}
