/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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
import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.psi.GoMethodDeclaration;
import com.goide.runconfig.GoRunUtil;
import com.goide.sdk.GoSdkService;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
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
    Project project = contextElement.getProject();
    if (module == null || !GoSdkService.getInstance(project).isGoModule(module)) return false;
    if (!myFramework.isAvailable(module)) return false;

    configuration.setModule(module);
    configuration.setTestFramework(myFramework);
    if (contextElement instanceof PsiDirectory) {
      configuration.setName(getPackageConfigurationName(((PsiDirectory)contextElement).getName()));
      configuration.setKind(GoTestRunConfiguration.Kind.DIRECTORY);
      String directoryPath = ((PsiDirectory)contextElement).getVirtualFile().getPath();
      configuration.setDirectoryPath(directoryPath);
      configuration.setWorkingDirectory(directoryPath);
      return true;
    }

    PsiFile file = contextElement.getContainingFile();
    if (myFramework.isAvailableOnFile(file)) {
      String importPath = ((GoFile)file).getImportPath(false);
      if (GoRunUtil.isPackageContext(contextElement) && StringUtil.isNotEmpty(importPath)) {
        configuration.setKind(GoTestRunConfiguration.Kind.PACKAGE);
        configuration.setPackage(importPath);
        configuration.setName(getPackageConfigurationName(importPath));
        return true;
      }
      else {
        GoFunctionOrMethodDeclaration function = findTestFunctionInContext(contextElement);
        if (function != null) {
          if (myFramework.isAvailableOnFunction(function)) {
            configuration.setName(getFunctionConfigurationName(function, file.getName()));
            configuration.setPattern("^" + function.getName() + "$");

            configuration.setKind(GoTestRunConfiguration.Kind.PACKAGE);
            configuration.setPackage(StringUtil.notNullize(((GoFile)file).getImportPath(false)));
            return true;
          }
        }
        else if (hasSupportedFunctions((GoFile)file)) {
          configuration.setName(getFileConfigurationName(file.getName()));
          configuration.setKind(GoTestRunConfiguration.Kind.FILE);
          configuration.setFilePath(file.getVirtualFile().getPath());
          return true;
        }
      }
    }
    return false;
  }

  private boolean hasSupportedFunctions(@NotNull GoFile file) {
    for (GoFunctionDeclaration declaration : file.getFunctions()) {
      if (myFramework.isAvailableOnFunction(declaration)) {
        return true;
      }
    }
    for (GoMethodDeclaration declaration : file.getMethods()) {
      if (myFramework.isAvailableOnFunction(declaration)) {
        return true;
      }
    }
    return false;
  }

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
        if (!Comparing.equal(((GoFile)file).getImportPath(false), configuration.getPackage())) return false;
        if (GoRunUtil.isPackageContext(contextElement) && configuration.getPattern().isEmpty()) return true;

        GoFunctionOrMethodDeclaration contextFunction = findTestFunctionInContext(contextElement);
        return contextFunction != null && myFramework.isAvailableOnFunction(contextFunction)
               ? configuration.getPattern().equals("^" + contextFunction.getName() + "$")
               : configuration.getPattern().isEmpty();
      case FILE:
        GoFunctionOrMethodDeclaration contextTestFunction = findTestFunctionInContext(contextElement);
        return contextTestFunction == null && GoTestFinder.isTestFile(file) && 
               FileUtil.pathsEqual(configuration.getFilePath(), file.getVirtualFile().getPath());
    }
    return false;
  }

  @Nullable
  private static GoFunctionOrMethodDeclaration findTestFunctionInContext(@NotNull PsiElement contextElement) {
    GoFunctionOrMethodDeclaration function = PsiTreeUtil.getNonStrictParentOfType(contextElement, GoFunctionOrMethodDeclaration.class);
    return function != null && GoTestFunctionType.fromName(function.getName()) != null ? function : null;
  }
}