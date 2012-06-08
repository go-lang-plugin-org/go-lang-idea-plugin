package ro.redeul.google.go.resolve;

import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import static ro.redeul.google.go.util.TestUtils.assertAs;
import static ro.redeul.google.go.util.TestUtils.assertParentType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 8, 2010
 * Time: 2:59:17 PM
 */
public class GoResolveTypesTestCase extends GoResolveTestCase {

    @Override
    protected String getRelativeDataPath() {
        return "types/";
    }

    public void testLocalType() throws Exception {
        PsiElement reference = resolve().resolve();

        GoTypeNameDeclaration typeName =
            assertAs(GoTypeNameDeclaration.class, reference);

        assertEquals("Type", typeName.getName());

        assertParentType(GoTypeSpec.class, typeName);
    }
}
