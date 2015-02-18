// This is a generated file. Not intended for manual editing.
package com.goide.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.goide.psi.impl.GoStringLiteralImpl;
import com.goide.util.GoStringLiteralEscaper;

public interface GoStringLiteral extends GoExpression, PsiLanguageInjectionHost {

  @Nullable
  PsiElement getRawString();

  @Nullable
  PsiElement getString();

  boolean isValidHost();

  @NotNull
  GoStringLiteralImpl updateText(String text);

  @NotNull
  GoStringLiteralEscaper createLiteralTextEscaper();

}
