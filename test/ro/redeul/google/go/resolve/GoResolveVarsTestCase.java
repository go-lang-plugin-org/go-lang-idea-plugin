package ro.redeul.google.go.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.statements.GoForWithClausesStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
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
                GoLiteral.class, variable));
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

    public void testSimpleMethodParameter() throws Exception {
        PsiElement resolved = resolve().resolve();

        GoIdentifier param = assertAs(GoIdentifier.class, resolved);
        assertEquals("x", param.getName());

        assertParentType(GoFunctionParameter.class, param);
    }
}
