package com.goide.editor;

import com.goide.psi.GoFile;
import com.goide.psi.GoImportDeclaration;
import com.goide.psi.GoImportSpec;
import com.goide.psi.GoImportString;
import com.intellij.codeInsight.editorActions.wordSelection.AbstractWordSelectioner;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoWordSelectioner extends AbstractWordSelectioner {
  @Override
  public boolean canSelect(@NotNull PsiElement e) {
    return e.getContainingFile() instanceof GoFile;
  }

  @Override
  public List<TextRange> select(@NotNull PsiElement e, CharSequence editorText, int cursorOffset, Editor editor) {
    final PsiElement parent = e.getParent();
    List<TextRange> result = super.select(e, editorText, cursorOffset, editor);
    if (parent instanceof GoImportString) {
      result.add(ElementManipulators.getValueTextRange(parent).shiftRight(parent.getTextRange().getStartOffset()));
    }
    else if (parent instanceof GoImportDeclaration) {
      List<GoImportSpec> specs = ((GoImportDeclaration)parent).getImportSpecList();
      GoImportSpec firstSpec = ContainerUtil.getFirstItem(specs);
      GoImportSpec lastSpec = ContainerUtil.getLastItem(specs);
      if (firstSpec != null && lastSpec != null) {
        result.add(TextRange.create(firstSpec.getTextRange().getStartOffset(),
                                    lastSpec.getTextRange().getEndOffset()));
      }
    }
    return result;
  }
}
