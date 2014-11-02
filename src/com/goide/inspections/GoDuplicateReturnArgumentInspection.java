package com.goide.inspections;

import com.goide.psi.*;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GoDuplicateReturnArgumentInspection extends GoDuplicateArgumentInspection {
  @Override
  public void check(@Nullable GoSignature o, @NotNull ProblemsHolder holder) {
    if (o == null) return;
    Set<String> names = getParamNames(o);
    GoResult result = o.getResult();
    if (result == null) return;
    GoParameters parameters = result.getParameters();
    if (parameters == null) return;
    List<GoParameterDeclaration> list = parameters.getParameterDeclarationList();
    for (GoParameterDeclaration declaration : list) {
      for (GoParamDefinition parameter : declaration.getParamDefinitionList()) {
        String text = parameter.getIdentifier().getText();
        if ("_".equals(text)) continue;
        if (names.contains(text)) {
          holder.registerProblem(parameter, errorText(text), ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
        }
        else {
          names.add(text);
        }
      }
    }
  }

  @NotNull
  private static Set<String> getParamNames(@NotNull GoSignature o) {
    List<GoParameterDeclaration> params = o.getParameters().getParameterDeclarationList();
    Set<String> names = new LinkedHashSet<String>();
    for (GoParameterDeclaration fp : params) {
      for (GoParamDefinition parameter : fp.getParamDefinitionList()) {
        String text = parameter.getIdentifier().getText();
        if ("_".equals(text)) continue;
        names.add(text);
      }
    }
    return names;
  }
}
