package ro.redeul.google.go.lang.completion.smartEnter.fixers;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.parser.GoElementTypes;

import static ro.redeul.google.go.lang.completion.smartEnter.fixers.FixerUtil.addEmptyBlockAtTheEndOfElement;
import static ro.redeul.google.go.lang.completion.smartEnter.fixers.FixerUtil.elementHasBlockChild;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class IfFixer implements SmartEnterFixer {
    @Override
    public boolean process(Editor editor, PsiElement psiElement) {
        if (isNodeOfType(psiElement, GoElementTypes.IF_STATEMENT) &&
            !elementHasBlockChild(psiElement)) {
            addEmptyBlockAtTheEndOfElement(editor, psiElement);
            return true;
        }

        return false;
    }
}
