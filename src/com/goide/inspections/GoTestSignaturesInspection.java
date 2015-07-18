/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.inspections;

import com.goide.GoConstants;
import com.goide.psi.*;
import com.goide.psi.impl.GoElementFactory;
import com.goide.runconfig.testing.GoTestFinder;
import com.goide.runconfig.testing.GoTestFunctionType;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class GoTestSignaturesInspection extends GoInspectionBase {
  private static String getTestingAlias(GoImportSpec testingImportSpec) {
    if (testingImportSpec != null) {
      return !testingImportSpec.isDot() ? StringUtil.notNullize(testingImportSpec.getAlias(), GoConstants.TESTING_PATH) : "";
    }
    return GoConstants.TESTING_PATH;
  }

  private static String getProperParamType(@NotNull String testingAlias, GoTestFunctionType type) {
    return "*" + (testingAlias.isEmpty() ? "" : testingAlias + ".") + type.getParamType();
  }

  @Override
  protected void checkFile(@NotNull final GoFile file, @NotNull ProblemsHolder problemsHolder) {
    if (!GoTestFinder.isTestFile(file)) return;
    GoImportSpec testingImportSpec = file.getImportedPackagesMap().get(GoConstants.TESTING_PATH);
    String testingAlias = getTestingAlias(testingImportSpec);
    for (GoFunctionDeclaration function : file.getFunctions()) {
      GoTestFunctionType type = GoTestFunctionType.fromName(function.getName());
      if (type == null) continue;
      GoSignature signature = function.getSignature();
      if (signature == null) continue;
      List<GoParameterDeclaration> params = signature.getParameters().getParameterDeclarationList();
      if (type == GoTestFunctionType.EXAMPLE) {
        if (!params.isEmpty()) {
          problemsHolder.registerProblem(function.getIdentifier(), "Wrong example signature", new GoTestSignaturesQuickFix(type));
        }
      }
      else {
        GoParameterDeclaration param = ContainerUtil.getFirstItem(params);
        if (params.size() != 1 ||
            param == null ||
            testingImportSpec == null ||
            !param.getType().textMatches(getProperParamType(testingAlias, type))) {
          problemsHolder.registerProblem(function.getIdentifier(), "Wrong test signature", new GoTestSignaturesQuickFix(type));
        }
      }
    }
  }

  private static class GoTestSignaturesQuickFix extends LocalQuickFixBase {
    private GoTestFunctionType myType;

    public GoTestSignaturesQuickFix(GoTestFunctionType type) {
      super("Fix signature");
      myType = type;
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      PsiElement function = descriptor.getStartElement().getParent();
      GoSignature signature =
        (function != null && function instanceof GoFunctionDeclaration) ? ((GoFunctionDeclaration)function).getSignature() : null;
      if (signature == null) return;
      PsiFile file = signature.getContainingFile();
      if (!(file instanceof GoFile)) return;
      if (myType == GoTestFunctionType.EXAMPLE) {
        signature.replace(GoElementFactory.createFunctionSignatureFromText(project, ""));
        return;
      }
      GoImportSpec testingImportSpec = (((GoFile)file).getImportedPackagesMap().get(GoConstants.TESTING_PATH));
      if (testingImportSpec == null) {
        ((GoFile)file).addImport(GoConstants.TESTING_PATH, null);
      }
      String paramType = getProperParamType(getTestingAlias(testingImportSpec), myType);
      signature
        .replace(GoElementFactory.createFunctionSignatureFromText(project, myType.getParamType().toLowerCase(Locale.US) + " " + paramType));
    }
  }
}
