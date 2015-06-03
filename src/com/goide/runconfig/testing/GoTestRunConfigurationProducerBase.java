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
import com.goide.psi.GoFunctionDeclaration;
import com.goide.runconfig.GoRunUtil;
import com.goide.sdk.GoSdkService;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationType;
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

public abstract class GoTestRunConfigurationProducerBase extends RunConfigurationProducer<GoTestRunConfigurationBase> {

  protected GoTestRunConfigurationProducerBase(@NotNull ConfigurationType configurationType) {
    super(configurationType);
  }

  @Override
  protected boolean setupConfigurationFromContext(@NotNull GoTestRunConfigurationBase configuration, ConfigurationContext context, Ref sourceElement) {
    PsiElement contextElement = GoRunUtil.getContextElement(context);
    if (contextElement == null) {
      return false;
    }

    Module module = ModuleUtilCore.findModuleForPsiElement(contextElement);
    if (module == null || !GoSdkService.getInstance(configuration.getProject()).isGoModule(module)) {
      return false;
    }
    
    configuration.setModule(module);
    if (contextElement instanceof PsiDirectory) {
      configuration.setName("All in '" + ((PsiDirectory)contextElement).getName() + "'");
      configuration.setKind(GoTestRunConfigurationBase.Kind.DIRECTORY);
      String directoryPath = ((PsiDirectory)contextElement).getVirtualFile().getPath();
      configuration.setDirectoryPath(directoryPath);
      configuration.setWorkingDirectory(directoryPath);
      return true;
    }
    else {
      PsiFile file = contextElement.getContainingFile();
      if (GoTestFinder.isTestFile(file)) {
        if (GoRunUtil.isPackageContext(contextElement)) {
          String packageName = StringUtil.notNullize(((GoFile)file).getImportPath());
          configuration.setKind(GoTestRunConfigurationBase.Kind.PACKAGE);
          configuration.setPackage(packageName);
          configuration.setName("All in '" + packageName + "'");
        }
        else {
          String functionNameFromContext = findFunctionNameFromContext(contextElement);
          if (functionNameFromContext != null) {
            configuration.setName(functionNameFromContext + " in " + file.getName());
            configuration.setPattern("^" + functionNameFromContext + "$");

            configuration.setKind(GoTestRunConfigurationBase.Kind.PACKAGE);
            configuration.setPackage(StringUtil.notNullize(((GoFile)file).getImportPath()));
          }
          else {
            configuration.setName(file.getName());
            configuration.setKind(GoTestRunConfigurationBase.Kind.FILE);
            configuration.setFilePath(file.getVirtualFile().getPath());
          }
        }
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean isConfigurationFromContext(@NotNull GoTestRunConfigurationBase configuration, ConfigurationContext context) {
    PsiElement contextElement = GoRunUtil.getContextElement(context);
    if (contextElement == null) return false;

    Module module = ModuleUtilCore.findModuleForPsiElement(contextElement);
    if (!Comparing.equal(module, configuration.getConfigurationModule().getModule())) return false;

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
        
        String functionNameFromContext = findFunctionNameFromContext(contextElement);
        return functionNameFromContext != null 
               ? configuration.getPattern().equals("^" + functionNameFromContext + "$") 
               : configuration.getPattern().isEmpty();
      case FILE:
        return GoTestFinder.isTestFile(file) && FileUtil.pathsEqual(configuration.getFilePath(), file.getVirtualFile().getPath()) &&
          findFunctionNameFromContext(contextElement) == null;
    }
    return false;
  }

  @Nullable
  private static String findFunctionNameFromContext(PsiElement contextElement) {
    GoFunctionDeclaration function = PsiTreeUtil.getNonStrictParentOfType(contextElement, GoFunctionDeclaration.class);
    return function != null ? GoTestFinder.getTestFunctionName(function) : null;
  }
}
