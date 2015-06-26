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

package com.goide.runconfig.testing.frameworks.gocheck;

import com.goide.psi.GoMethodDeclaration;
import com.goide.runconfig.GoRunUtil;
import com.goide.runconfig.testing.GoTestFinder;
import com.goide.runconfig.testing.GoTestRunConfigurationProducerBase;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class GocheckRunConfigurationProducer extends GoTestRunConfigurationProducerBase implements Cloneable {
  public GocheckRunConfigurationProducer() {
    super(GocheckRunConfigurationType.getInstance());
  }

  @NotNull
  @Override
  protected String getPackageConfigurationName(@NotNull String packageName) {
    return "gocheck package '" + packageName + "'";
  }

  @NotNull
  @Override
  protected String getFunctionConfigurationName(@NotNull String functionName, @NotNull String fileName) {
    return "gocheck function " + super.getFunctionConfigurationName(functionName, fileName);
  }

  @NotNull
  @Override
  protected String getFileConfigurationName(@NotNull String fileName) {
    return "gocheck " + super.getFileConfigurationName(fileName);
  }

  @Override
  protected boolean isAvailableInModule(@Nullable Module module) {
    return super.isAvailableInModule(module) && GoRunUtil.hasGoCheckSupport(module);
  }

  @Nullable
  @Override
  protected String findFunctionNameFromContext(PsiElement contextElement) {
    GoMethodDeclaration method = PsiTreeUtil.getNonStrictParentOfType(contextElement, GoMethodDeclaration.class);
    return method != null ? GoTestFinder.getTestFunctionName(method) : null;
  }
}
