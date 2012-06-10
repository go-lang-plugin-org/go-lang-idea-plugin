package ro.redeul.google.go.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoForWithClausesStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import static ro.redeul.google.go.util.TestUtils.assertAs;
import static ro.redeul.google.go.util.TestUtils.assertParentType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 8, 2010
 * Time: 2:59:17 PM
 */
public class GoResolveVarsTestCase extends GoResolveTestCase {

    @Override
    protected String getRelativeDataPath() {
        return "vars/";
    }

    public void testDeclaredInForRange() throws Exception {
        PsiReference reference = resolve();
        PsiElement psiElement = reference.resolve();

        GoIdentifier variable = assertAs(GoIdentifier.class, psiElement);
        assertEquals("key", variable.getName());

        assertParentType(
            GoForWithRangeStatement.class,
            assertParentType(
                GoLiteralExpression.class, variable));
    }

    public void testDeclaredInForRange2() throws Exception {
        PsiElement psiElement = resolve().resolve();

        GoIdentifier variable = assertAs(GoIdentifier.class, psiElement);
        assertEquals("i", variable.getName());

        assertParentType(
            GoForWithRangeStatement.class,
            assertParentType(
                GoLiteralExpression.class, variable));
    }

    public void testDeclaredInForRangeAsValue() throws Exception {
        PsiReference reference = resolve();
        PsiElement psiElement = reference.resolve();

        GoIdentifier variable = assertAs(GoIdentifier.class, psiElement);
        assertEquals("key2", variable.getName());

        assertParentType(
            GoForWithRangeStatement.class,
            assertParentType(
                GoLiteralExpression.class, variable));
    }

    public void testDeclaredInForClause() throws Exception {
        PsiElement psiElement = resolve().resolve();

        GoIdentifier variable = assertAs(GoIdentifier.class, psiElement);
        assertEquals("i", variable.getName());

        assertParentType(
            GoForWithClausesStatement.class,
            assertParentType(
                GoShortVarDeclaration.class, variable));
    }

    public void testMethodReturn() throws Exception {
        PsiElement resolved = resolve().resolve();

        GoIdentifier param = assertAs(GoIdentifier.class, resolved);
        assertEquals("c", param.getName());

        GoFunctionDeclaration functionDeclaration =
            assertParentType(
                GoFunctionDeclaration.class,
                assertParentType(
                    GoElementTypes.FUNCTION_RESULT,
                    assertParentType(
                        GoElementTypes.FUNCTION_PARAMETER_LIST,
                        assertParentType(GoFunctionParameter.class,
                                         param))));

        assertEquals("function", functionDeclaration.getFunctionName());
    }

    public void testMethodReturn2() throws Exception {
        PsiElement resolved = resolve().resolve();

        GoIdentifier param = assertAs(GoIdentifier.class, resolved);
        assertEquals("g1v", param.getName());

        GoFunctionDeclaration functionDeclaration =
            assertParentType(
                GoFunctionDeclaration.class,
                assertParentType(
                    GoElementTypes.FUNCTION_RESULT,
                    assertParentType(
                        GoElementTypes.FUNCTION_PARAMETER_LIST,
                        assertParentType(GoFunctionParameter.class,
                                         param))));

        assertEquals("gen1", functionDeclaration.getFunctionName());
    }
}
