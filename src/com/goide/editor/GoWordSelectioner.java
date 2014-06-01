package com.goide.editor;

import com.goide.psi.*;
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
      result.addAll(extend(editorText, ((GoImportDeclaration)parent).getImportSpecList(), false));
    }
    else if (e instanceof GoSimpleStatement) {
      result.addAll(expandToWholeLine(editorText, e.getTextRange()));
    }
    else if (e instanceof GoBlock) {
      result.addAll(extend(editorText, ((GoBlock)e).getStatementList(), true));
    }
    return result;
  }

  @NotNull
  private static List<TextRange> extend(@NotNull CharSequence editorText, @NotNull List<? extends PsiElement> list, boolean expand) {
    PsiElement first = ContainerUtil.getFirstItem(list);
    PsiElement last = ContainerUtil.getLastItem(list);
    if (first != null && last != null) {
      TextRange range = TextRange.create(first.getTextRange().getStartOffset(), last.getTextRange().getEndOffset());
      if (!expand) return ContainerUtil.newSmartList(range);
      return expandToWholeLine(editorText, range);
    }
    return ContainerUtil.emptyList();
  }
}
