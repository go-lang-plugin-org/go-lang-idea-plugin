package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFloat;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;
import static ro.redeul.google.go.util.GoPsiTestUtils.getAs;

public class GoPsiFloatTest extends GoPsiTestCase {


    public void testBasic() throws Exception {
        GoFile file = get(
                parse("" +
                        "package main\n" +
                        "var (\n" +
                        "     x = 10.0\n" +
                        "     x1 = .25\n" +
                        "     y = 0.\n" +
                        "     z = 072.40\n" +
                        "     e1 = 1.e+0\n" +
                        "     e2 = 6.67428e-11\n" +
                        "     e3 = 1E6\n" +
//                        "     h2 = 0XAB\n" +
                        "}"));

        GoVarDeclaration[] declarations =
                childAt(0,
                        file.getGlobalVariables()
                ).getDeclarations();

        GoLiteralFloat fl;

        // x
        fl =
                getAs(GoLiteralFloat.class,
                        getAs(GoLiteralExpression.class,
                                childAt(0,
                                        declarations[0].getExpressions()
                                )
                        ).getLiteral()
                );

        assertEquals(GoLiteral.Type.Float, fl.getType());
        assertEquals((float) 10.0, fl.getValue());

        // x
        fl =
                getAs(GoLiteralFloat.class,
                        getAs(GoLiteralExpression.class,
                                childAt(0,
                                        declarations[1].getExpressions()
                                )
                        ).getLiteral()
                );

        assertEquals(GoLiteral.Type.Float, fl.getType());
        assertEquals((float) 0.25, fl.getValue());

        // y
        fl =
                getAs(GoLiteralFloat.class,
                        getAs(GoLiteralExpression.class,
                                childAt(0,
                                        declarations[2].getExpressions()
                                )
                        ).getLiteral()
                );

        assertEquals(GoLiteral.Type.Float, fl.getType());
        assertEquals((float) 0.0, fl.getValue());

        // z
        fl =
                getAs(GoLiteralFloat.class,
                        getAs(GoLiteralExpression.class,
                                childAt(0,
                                        declarations[3].getExpressions()
                                )
                        ).getLiteral()
                );

        assertEquals(GoLiteral.Type.Float, fl.getType());
        assertEquals((float) 72.40, fl.getValue());

        // e1
        fl =
                getAs(GoLiteralFloat.class,
                        getAs(GoLiteralExpression.class,
                                childAt(0,
                                        declarations[4].getExpressions()
                                )
                        ).getLiteral()
                );

        assertEquals(GoLiteral.Type.Float, fl.getType());
        assertEquals((float) 1.e+0, fl.getValue());

        // e2
        fl =
                getAs(GoLiteralFloat.class,
                        getAs(GoLiteralExpression.class,
                                childAt(0,
                                        declarations[5].getExpressions()
                                )
                        ).getLiteral()
                );

        assertEquals(GoLiteral.Type.Float, fl.getType());
        assertEquals((float) 6.67428e-11, fl.getValue());

        // e3
        fl =
                getAs(GoLiteralFloat.class,
                        getAs(GoLiteralExpression.class,
                                childAt(0,
                                        declarations[6].getExpressions()
                                )
                        ).getLiteral()
                );

        assertEquals(GoLiteral.Type.Float, fl.getType());
        assertEquals((float) 1E6, fl.getValue());

    }
}
