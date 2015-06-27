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

package com.goide.runconfig.testing;

import com.goide.psi.GoFile;
import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.runconfig.GoRunUtil;
import com.goide.sdk.GoSdkService;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GoTestRunConfigurationProducerBase extends RunConfigurationProducer<GoTestRunConfiguration> {

  @NotNull private final GoTestFramework myFramework;

  protected GoTestRunConfigurationProducerBase(@NotNull GoTestFramework framework) {
    super(GoTestRunConfigurationType.getInstance());
    myFramework = framework;
  }

  @Override
  protected boolean setupConfigurationFromContext(@NotNull GoTestRunConfiguration configuration,
                                                  ConfigurationContext context,
                                                  Ref sourceElement) {
    PsiElement contextElement = GoRunUtil.getContextElement(context);
    if (contextElement == null) {
      return false;
    }

    Module module = ModuleUtilCore.findModuleForPsiElement(contextElement);
    if (module == null || !GoSdkService.getInstance(module.getProject()).isGoModule(module)) return false;
    if (!myFramework.isAvailable(module)) return false;

    configuration.setModule(module);
    configuration.setTestFramework(myFramework);
    if (contextElement instanceof PsiDirectory) {
      for (PsiFile file : ((PsiDirectory)contextElement).getFiles()) {
        if (GoTestFinder.isTestFile(file)) {
          configuration.setName(getPackageConfigurationName(((PsiDirectory)contextElement).getName()));
          configuration.setKind(GoTestRunConfiguration.Kind.DIRECTORY);
          String directoryPath = ((PsiDirectory)contextElement).getVirtualFile().getPath();
          configuration.setDirectoryPath(directoryPath);
          configuration.setWorkingDirectory(directoryPath);
          return true;
        }
      }
      return false;
    }
    else {
      PsiFile file = contextElement.getContainingFile();
      if (GoTestFinder.isTestFile(file)) {
        if (GoRunUtil.isPackageContext(contextElement)) {
          String packageName = StringUtil.notNullize(((GoFile)file).getImportPath());
          configuration.setKind(GoTestRunConfiguration.Kind.PACKAGE);
          configuration.setPackage(packageName);
          configuration.setName(getPackageConfigurationName(packageName));
        }
        else {
          GoFunctionOrMethodDeclaration function = findTestFunctionInContext(contextElement);
          if (shouldSkipContext(function)) return false;

          if (function != null) {
            configuration.setName(getFunctionConfigurationName(function, getFileConfigurationName(file.getName())));
            configuration.setPattern("^" + function.getName() + "$");

            configuration.setKind(GoTestRunConfiguration.Kind.PACKAGE);
            configuration.setPackage(StringUtil.notNullize(((GoFile)file).getImportPath()));
          }
          else {
            configuration.setName(getFileConfigurationName(file.getName()));
            configuration.setKind(GoTestRunConfiguration.Kind.FILE);
            configuration.setFilePath(file.getVirtualFile().getPath());
          }
        }
        return true;
      }
    }

    return false;
  }

  protected abstract boolean shouldSkipContext(@Nullable GoFunctionOrMethodDeclaration context);

  @NotNull
  protected String getFileConfigurationName(@NotNull String fileName) {
    return fileName;
  }

  @NotNull
  protected String getFunctionConfigurationName(@NotNull GoFunctionOrMethodDeclaration function, @NotNull String fileName) {
    return function.getName() + " in " + fileName;
  }

  @NotNull
  protected String getPackageConfigurationName(@NotNull String packageName) {
    return "All in '" + packageName + "'";
  }

  @Override
  public boolean isConfigurationFromContext(@NotNull GoTestRunConfiguration configuration, ConfigurationContext context) {
    PsiElement contextElement = GoRunUtil.getContextElement(context);
    if (contextElement == null) return false;

    Module module = ModuleUtilCore.findModuleForPsiElement(contextElement);
    if (!Comparing.equal(module, configuration.getConfigurationModule().getModule())) return false;
    if (!Comparing.equal(myFramework, configuration.getTestFramework())) return false;

    PsiFile file = contextElement.getContainingFile();
    switch (configuration.getKind()) {
      case DIRECTORY:
        if (contextElement instanceof PsiDirectory) {
          String directoryPath = ((PsiDirectory)contextElement).getVirtualFile().getPath();
          return FileUtil.pathsEqual(configuration.getDirectoryPath(), directoryPath) &&
                 FileUtil.pathsEqual(configuration.getWorkingDirectory(), directoryPath);
        }
      case PACKAGE:
        if (!GoTestFinder.isTestFile(file)) return false;
        if (!Comparing.equal(((GoFile)file).getImportPath(), configuration.getPackage())) return false;
        if (GoRunUtil.isPackageContext(contextElement) && configuration.getPattern().isEmpty()) return true;

        GoFunctionOrMethodDeclaration contextFunction = findTestFunctionInContext(contextElement);
        return contextFunction != null
               ? configuration.getPattern().equals("^" + contextFunction.getName() + "$")
               : configuration.getPattern().isEmpty();
      case FILE:
        return GoTestFinder.isTestFile(file) && FileUtil.pathsEqual(configuration.getFilePath(), file.getVirtualFile().getPath()) &&
               findTestFunctionInContext(contextElement) == null;
    }
    return false;
  }

  @Nullable
  private static GoFunctionOrMethodDeclaration findTestFunctionInContext(@NotNull PsiElement contextElement) {
    GoFunctionOrMethodDeclaration function = PsiTreeUtil.getNonStrictParentOfType(contextElement, GoFunctionOrMethodDeclaration.class);
    return function != null && GoTestFinder.isTestFunctionName(function.getName()) ? function : null;
  }
}
