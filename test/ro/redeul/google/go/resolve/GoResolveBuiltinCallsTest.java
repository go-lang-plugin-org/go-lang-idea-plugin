package ro.redeul.google.go.resolve;

import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 8, 2010
 * Time: 2:59:17 PM
 */
public class GoResolveBuiltinCallsTest extends GoPsiResolveTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "builtins/";
    }

    @Override
    protected void assertTest() {
        assertNotNull("Source position is not at a reference", ref);


        PsiElement resolvedDefinition = ref.resolve();
        assertNotNull("The resolving should have been been a success",
                      resolvedDefinition);

        while (resolvedDefinition.getStartOffsetInParent() == 0) {
            resolvedDefinition = resolvedDefinition.getParent();
        }

        assertTrue("It resolved to the name of a function",
                   psiElement(GoLiteralIdentifier.class)
                       .withParent(GoFunctionDeclaration.class)
                       .accepts(resolvedDefinition)
        );
    }

    public void testMethodName() throws Exception {
        doTest();
    }
}
