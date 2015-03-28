package com.intellij.appengine;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AppEngineExtension {
  private static final ExtensionPointName<AppEngineExtension> EXTENSION = ExtensionPointName.create("ro.redeul.google.go.appengine.Extension");

  private static boolean ourTestingMode = false;

  public static void enableTestingMode(@NotNull Disposable disposable) {
    ourTestingMode = true;
    Disposer.register(disposable, new Disposable() {
      @Override
      public void dispose() {
        //noinspection AssignmentToStaticFieldFromInstanceMethod
        ourTestingMode = false;
      }
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