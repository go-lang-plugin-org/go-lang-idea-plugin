package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoVarSpec;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;
import static ro.redeul.google.go.util.GoPsiTestUtils.getAs;

public class GoPsiStringsTest extends GoPsiTestCase {


    public void testBasic() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "var (\n" +
                      "     x = \"a\"\n" +
                      "     y = `b`\n" +
                      "}"));

        GoVarSpec[] declarations =
            childAt(0,
                    file.getGlobalVariables()
            ).getDeclarations();

        GoLiteralString string;

        string =
            getAs(GoLiteralString.class,
                  getAs(GoLiteralExpression.class,
                        childAt(0,
                                declarations[0].getExpressions()
                        )
                  ).getLiteral()
            );

        assertEquals(GoLiteral.Type.InterpretedString, string.getType());

        string =
            getAs(GoLiteralString.class,
                  getAs(GoLiteralExpression.class,
                        childAt(0,
                                declarations[1].getExpressions()
                        )
                  ).getLiteral()
            );

        assertEquals(GoLiteral.Type.RawString, string.getType());
    }
}
