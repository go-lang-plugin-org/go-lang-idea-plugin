package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralInteger;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;
import static ro.redeul.google.go.util.GoPsiTestUtils.getAs;

public class GoPsiIntegerTest extends GoPsiTestCase {


    public void testBasic() throws Exception {
        GoFile file = get(
                parse("" +
                        "package main\n" +
                        "var (\n" +
                        "     x = 10\n" +
                        "     y = 0\n" +
                        "     z = 0120\n" +
                        "     h1 = 0xEF\n" +
                        "     h2 = 0XAB\n" +
                        "}"));

        GoVarDeclaration[] declarations =
                childAt(0,
                        file.getGlobalVariables()
                ).getDeclarations();

        GoLiteralInteger integer;

        // x
        integer =
                getAs(GoLiteralInteger.class,
                        getAs(GoLiteralExpression.class,
                                childAt(0,
                                        declarations[0].getExpressions()
                                )
                        ).getLiteral()
                );

        assertEquals(GoLiteral.Type.Int, integer.getType());
        assertEquals((Integer) 10, integer.getValue());

        // y
        integer =
                getAs(GoLiteralInteger.class,
                        getAs(GoLiteralExpression.class,
                                childAt(0,
                                        declarations[1].getExpressions()
                                )
                        ).getLiteral()
                );
        assertEquals(GoLiteral.Type.Int, integer.getType());
        assertEquals((Integer) 0, integer.getValue());

        // z
        integer =
                getAs(GoLiteralInteger.class,
                        getAs(GoLiteralExpression.class,
                                childAt(0,
                                        declarations[2].getExpressions()
                                )
                        ).getLiteral()
                );
        assertEquals(GoLiteral.Type.Int, integer.getType());
        assertEquals((Integer) 0120, integer.getValue());

        // h1
        integer =
                getAs(GoLiteralInteger.class,
                        getAs(GoLiteralExpression.class,
                                childAt(0,
                                        declarations[3].getExpressions()
                                )
                        ).getLiteral()
                );
        assertEquals(GoLiteral.Type.Int, integer.getType());
        assertEquals((Integer) 0xEF, integer.getValue());

        // h1
        integer =
                getAs(GoLiteralInteger.class,
                        getAs(GoLiteralExpression.class,
                                childAt(0,
                                        declarations[4].getExpressions()
                                )
                        ).getLiteral()
                );
        assertEquals(GoLiteral.Type.Int, integer.getType());
        assertEquals((Integer) 0xAB, integer.getValue());

    }
}
