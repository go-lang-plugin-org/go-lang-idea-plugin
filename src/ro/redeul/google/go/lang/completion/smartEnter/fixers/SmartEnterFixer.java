package ro.redeul.google.go.lang.completion.smartEnter.fixers;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;

public interface SmartEnterFixer {
    boolean process(Editor editor, PsiElement psiElement);
}
