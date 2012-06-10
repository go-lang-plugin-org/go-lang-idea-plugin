package ro.redeul.google.go.lang.completion.smartEnter.fixers;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import static ro.redeul.google.go.lang.completion.smartEnter.fixers.FixerUtil.addEmptyBlockAtTheEndOfElement;
import static ro.redeul.google.go.lang.completion.smartEnter.fixers.FixerUtil.elementHasBlockChild;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class FunctionFixer implements SmartEnterFixer {
    @Override
    public boolean process(Editor editor, PsiElement psiElement) {
        // complete case: go func()<caret>
        if (isNodeOfType(psiElement, GoElementTypes.GO_STATEMENT) &&
                isNodeOfType(psiElement.getLastChild(), GoElementTypes.TYPE_FUNCTION)) {
            addEmptyBlockAtTheEndOfElement(editor, psiElement);
            return true;
        }

        PsiElement parent = psiElement.getParent();

        // complete case: func Foo() (int, int<caret>)
        PsiElement functionResult = findFunctionResultParent(parent);
        if (functionResult != null) {
            parent = functionResult.getParent();
        }

        // complete case: func Foo (a, b int<caret>)
        PsiElement parameterList = findFunctionParameterListParent(parent);
        if (parameterList != null) {
            parent = parameterList.getParent();
        }

        if (isNodeOfType(parent, GoElementTypes.TYPE_FUNCTION)) {
            parent = parent.getParent();
            if (!elementHasBlockChild(parent)) {
                addEmptyBlockAtTheEndOfElement(editor, parent);
                return true;
            }
        }

        if (parent instanceof GoFunctionDeclaration ||
                parent instanceof GoLiteralFunction) {
            if (!elementHasBlockChild(parent)) {
                addEmptyBlockAtTheEndOfElement(editor, parent);
                return true;
            }
        }

        return false;
    }

    private PsiElement findFunctionResultParent(PsiElement element) {
        if (isNodeOfType(element, GoElementTypes.FUNCTION_RESULT)) {
            return element;
        }

        return findParentOfType(element, GoElementTypes.FUNCTION_RESULT);
    }

    private PsiElement findFunctionParameterListParent(PsiElement element) {
        if (isNodeOfType(element, GoElementTypes.FUNCTION_PARAMETER_LIST)) {
            return element;
        }

        return findParentOfType(element, GoElementTypes.FUNCTION_PARAMETER_LIST);
    }
}
