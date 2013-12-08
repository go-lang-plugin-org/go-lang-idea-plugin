package ro.redeul.google.go.lang.completion.smartEnter.fixers;

import com.intellij.openapi.editor.Editor;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.statements.GoDeferStatement;
import ro.redeul.google.go.lang.psi.statements.GoGoStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;
import static ro.redeul.google.go.lang.completion.smartEnter.fixers.FixerUtil.addEmptyBlockAtTheEndOfElement;
import static ro.redeul.google.go.lang.completion.smartEnter.fixers.FixerUtil.elementHasBlockChild;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class FunctionFixer implements SmartEnterFixer {
    private static final TokenSet GO_OR_DEFER = TokenSet.create(
            GoElementTypes.GO_STATEMENT,
            GoElementTypes.DEFER_STATEMENT
    );

    @SuppressWarnings("unchecked")
    private static final PsiElementPattern.Capture<GoLiteralFunction> LITERAL_FUNCTION_IN_GO_OR_DEFER =
            psiElement(GoLiteralFunction.class)
            .withParent(
                psiElement(GoLiteralExpression.class)
                    .withParent(
                        or(
                            psiElement(GoGoStatement.class),
                            psiElement(GoDeferStatement.class)
                        )
                    )
            );

    @Override
    public boolean process(Editor editor, PsiElement psiElement) {
        // complete case: go func()<caret>
        if (isNodeOfType(psiElement, GO_OR_DEFER) &&
            isNodeOfType(psiElement.getLastChild(), GoElementTypes.LITERAL_EXPRESSION)) {

            PsiElement literal = psiElement.getLastChild();
            if (literal == null) {
                return false;
            }

            PsiElement func = literal.getLastChild();
            if (isNodeOfType(func, GoElementTypes.LITERAL_FUNCTION) &&
                !elementHasBlockChild(func)) {
                addEmptyBlockAndParentheses(editor, psiElement);
                return true;
            }
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

        if (isNodeOfType(parent, GoElementTypes.LITERAL_FUNCTION)) {
            if (!elementHasBlockChild(parent)) {
                if (isLiteralFunctionInGoOrDefer(parent)) {
                    addEmptyBlockAndParentheses(editor, parent);
                } else {
                    addEmptyBlockAtTheEndOfElement(editor, parent);
                }
                return true;
            }
        }

        if (parent instanceof GoFunctionDeclaration) {
            if (!elementHasBlockChild(parent)) {
                addEmptyBlockAtTheEndOfElement(editor, parent);
                return true;
            }
        }

        return false;
    }

    /**
     * For "func()" element add an empty block and parentheses, make it looks like:
     *   func() {
     *   }()
     * @param editor the editor
     * @param psiElement the "func()" element
     */
    private static void addEmptyBlockAndParentheses(Editor editor, PsiElement psiElement) {
        addEmptyBlockAtTheEndOfElement(editor, psiElement, "{\n}()");
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

    private static boolean isLiteralFunctionInGoOrDefer(PsiElement element) {
        return LITERAL_FUNCTION_IN_GO_OR_DEFER.accepts(element);
    }
}
