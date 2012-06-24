package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import static ro.redeul.google.go.util.GoPsiTestUtils.castAs;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;
import static ro.redeul.google.go.util.GoPsiTestUtils.getAs;

public class GoPsiFunctionLiteralTestCase extends GoPsiTestCase {
    public void testReturn() throws Exception {

        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "func main() {\n" +
                      "   f := func() int {\n" +
                      "            return 3\n" +
                      "        };\n" +
                      "}"));

        GoLiteralFunction lit =
            getAs(GoLiteralFunction.class,
                  castAs(GoLiteralExpression.class, 0,
                         castAs(GoShortVarDeclaration.class, 0,
                                get(
                                    childAt(0,
                                            file.getFunctions()
                                    ).getBlock()
                                ).getStatements()
                         ).getExpressions()
                  ).getLiteral());

        assertNotNull(lit.getResults());
        assertNotNull(lit.getParameters());
        assertNotNull(lit.getBlock());

        assertEquals("int", castAs(GoFunctionParameter.class, 0, lit.getResults()).getText());
    }
}
