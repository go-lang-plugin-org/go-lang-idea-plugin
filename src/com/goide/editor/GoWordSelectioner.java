package com.goide.editor;

import com.goide.psi.GoFile;
import com.goide.psi.GoImportString;
import com.intellij.codeInsight.editorActions.wordSelection.AbstractWordSelectioner;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;

import java.util.List;

public class GoWordSelectioner extends AbstractWordSelectioner {
  @Override
  public boolean canSelect(PsiElement e) {
    return e.getContainingFile() instanceof GoFile;
  }

  @Override
  public List<TextRange> select(PsiElement e, CharSequence editorText, int cursorOffset, Editor editor) {
    final PsiElement parent = e.getParent();
    List<TextRange> result = super.select(e, editorText, cursorOffset, editor);
    if (parent instanceof GoImportString) {
      result.add(ElementManipulators.getValueTextRange(parent).shiftRight(parent.getTextRange().getStartOffset()));
    }
    return result;
  }
}
