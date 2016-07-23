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

package com.intellij.appengine;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AppEngineExtension {
  private static final ExtensionPointName<AppEngineExtension> EXTENSION = ExtensionPointName.create("ro.redeul.google.go.appengine.Extension");

  private static boolean ourTestingMode;

  public static void enableTestingMode(@NotNull Disposable disposable) {
    ourTestingMode = true;
    Disposer.register(disposable, () -> {
      //noinspection AssignmentToStaticFieldFromInstanceMethod
      ourTestingMode = false;
    });
  }

  public abstract boolean isAppEngineEnabled(@Nullable PsiElement context);

  public static boolean isAppEngineContext(@Nullable PsiElement context) {
    for (AppEngineExtension extension : EXTENSION.getExtensions()) {
      if (extension.isAppEngineEnabled(context)) {
        return true;
      }
    }
    return ourTestingMode;
  }
}