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

package com.goide.appengine;

import com.goide.sdk.GoSdkService;
import com.intellij.appengine.AppEngineExtension;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public class GoAppEngineExtension extends AppEngineExtension {
  @Override
  public boolean isAppEngineEnabled(@Nullable PsiElement context) {
    if (context != null) {
      // it's important to ask module on file, otherwise module won't be found for elements in libraries files [zolotov]
      Module module = ModuleUtilCore.findModuleForPsiElement(context.getContainingFile());
      if (GoSdkService.getInstance(context.getProject()).isAppEngineSdk(module)) {
        return true;
      }
    }
    return false;
  }
}
