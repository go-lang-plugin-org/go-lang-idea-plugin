package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import static ro.redeul.google.go.util.GoPsiTestUtils.castAs;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;
import static ro.redeul.google.go.util.GoPsiTestUtils.getAs;

public class GoPsiBuiltinCallExpressionTest extends GoPsiTestCase {


    public void testNew() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "var e = new(int)"));

        GoBuiltinCallExpression builtin =
            castAs(GoBuiltinCallExpression.class, 0,
                 childAt(0,
                         childAt(0,
                                 file.getGlobalVariables()
                         ).getDeclarations()
                 ).getExpressions()
            );

        assertEquals("int", getAs(GoPsiTypeName.class, builtin.getTypeArgument()).getText());
        assertEquals("new", get(builtin.getBaseExpression()).getText());
    }

    public void testMake() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "var e = make(int, 1)"));

        GoBuiltinCallExpression builtin =
            castAs(GoBuiltinCallExpression.class, 0,
                 childAt(0,
                         childAt(0,
                                 file.getGlobalVariables()
                         ).getDeclarations()
                 ).getExpressions()
            );

        assertEquals("int", getAs(GoPsiTypeName.class, builtin.getTypeArgument()).getText());
        assertEquals(builtin.getArguments().length, 1);
    }
}
