package ro.redeul.google.go.lang.completion.smartEnter.fixers;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.statements.GoForStatement;

import static ro.redeul.google.go.lang.completion.smartEnter.fixers.FixerUtil.addEmptyBlockAtTheEndOfElement;
import static ro.redeul.google.go.lang.completion.smartEnter.fixers.FixerUtil.elementHasBlockChild;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class ForFixer implements SmartEnterFixer {
    @Override
    public boolean process(Editor editor, PsiElement psiElement) {
        psiElement = findParentOfType(psiElement, GoForStatement.class);
        if (isNodeOfType(psiElement, GoElementTypes.STMTS_FOR) &&
            !elementHasBlockChild(psiElement)) {
            addEmptyBlockAtTheEndOfElement(editor, psiElement);
            return true;
        }
        return false;
    }
}
