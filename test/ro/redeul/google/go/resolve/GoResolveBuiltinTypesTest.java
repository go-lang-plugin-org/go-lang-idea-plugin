package ro.redeul.google.go.resolve;

import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import static ro.redeul.google.go.util.GoPsiTestUtils.getAs;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 8, 2010
 * Time: 2:59:17 PM
 */
public class GoResolveBuiltinTypesTest extends GoPsiResolveTestCase {

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

        getAs(GoTypeSpec.class, resolvedDefinition);
    }

    public void testBuiltinTypes() throws Exception {
        doTest();
    }

    public void testVarBuiltinType() throws Exception {
        doTest();
    }

    public void testVarMethodType() throws Exception {
        doTest();
    }

    public void testParameterType() throws Exception {
        doTest();
    }
}
