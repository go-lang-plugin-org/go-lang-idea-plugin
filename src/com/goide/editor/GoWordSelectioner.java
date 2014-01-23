package com.goide.editor;

import com.goide.psi.GoFile;
import com.intellij.codeInsight.editorActions.wordSelection.AbstractWordSelectioner;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;

import java.util.ArrayList;
import java.util.List;

public class GoWordSelectioner extends AbstractWordSelectioner {
  @Override
  public boolean canSelect(PsiElement e) {
    return e.getContainingFile() instanceof GoFile;
  }

  @Override
  public List<TextRange> select(PsiElement e, CharSequence editorText, int cursorOffset, Editor editor) {
    final PsiElement parent = e.getParent();
    //if (parent instanceof LESSMixinName) {
    //  final List<TextRange> result = ContainerUtil.newArrayList();
    //  result.add(parent.getTextRange());
    //  final PsiElement mixinInvocation = parent.getParent();
    //  if (mixinInvocation instanceof LESSMixinInvocation) {
    //    result.add(mixinInvocation.getTextRange());
    //    final List<LESSNamespace> namespaces = ((LESSMixinInvocation)mixinInvocation).getNamespacesElements();
    //    for (LESSNamespace namespace : namespaces) {
    //      result.add(TextRange.create(namespace.getTextRange().getStartOffset(), mixinInvocation.getTextRange().getEndOffset()));
    //    }
    //  }
    //  return result;
    //}
    //
    //LessStatement statement = PsiTreeUtil.getParentOfType(e, LessStatement.class);
    //if (statement != null) {
    //  ArrayList<TextRange> result = ContainerUtil.newArrayList(statement.getTextRange());
    //  ContainerUtil.addIfNotNull(result, CssSelectioner.getDeclarationWithSemicolonRange(statement));
    //  return result;
    //}
    //if (e.getLanguage().is(LESSLanguage.INSTANCE)) {
    //  return super.select(e, editorText, cursorOffset, editor);
    //}
    return super.select(e, editorText, cursorOffset, editor);
  }
}
