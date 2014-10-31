package com.goide.spellchecker;

import com.goide.GoLanguage;
import com.intellij.psi.PsiElement;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import org.jetbrains.annotations.NotNull;

public class GoSpellcheckingStrategy extends SpellcheckingStrategy {
  @Override
  public boolean isMyContext(@NotNull PsiElement element) {
    return GoLanguage.INSTANCE.is(element.getLanguage());
  }
}
