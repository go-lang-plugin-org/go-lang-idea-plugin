package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                      "     z = '本'\n" +
                      "     w = '\\U00101234'\n" +
                      "}"));

        GoVarDeclaration[] declarations =
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
        assertEquals("a", string.getValue());

        string =
            getAs(GoLiteralString.class,
                  getAs(GoLiteralExpression.class,
                        childAt(0,
                                declarations[1].getExpressions()
                        )
                  ).getLiteral()
            );

        assertEquals(GoLiteral.Type.RawString, string.getType());
        assertEquals("b", string.getValue());


        string =
                getAs(GoLiteralString.class,
                        getAs(GoLiteralExpression.class,
                                childAt(0,
                                        declarations[2].getExpressions()
                                )
                        ).getLiteral()
                );

        assertEquals(GoLiteral.Type.InterpretedString, string.getType());
        assertEquals("本", string.getValue());

        string =
                getAs(GoLiteralString.class,
                        getAs(GoLiteralExpression.class,
                                childAt(0,
                                        declarations[3].getExpressions()
                                )
                        ).getLiteral()
                );

        assertEquals(GoLiteral.Type.InterpretedString, string.getType());
        assertEquals("ሴ", string.getValue());

    }

    public void testRune() throws Exception {
        GoFile file = get(
                parse("" +
                        "package main\n" +
                        "var (\n" +
                        "     x0 = '1'\n" +
                        "     x1 = 'a'\n" +
                        "     x2 = 'ä'\n" +
                        "     x3 = '本'\n" +
                        "     x4 = '\\t'\n" +
                        "     x5 = '\\000'\n" +
                        "     x6 = '\\007'\n" +
                        "     x7 = '\\377'\n" +
                        "     x8 = '\\x07'\n" +
                        "     x9 = '\\xff'\n" +
                        "     x10 = '\\u12e4'\n" +
                        "     x11 = '\\U00101234'\n" +
                        "     x12 = '\\\\'\n" +
                        "     x13 = '\\\''\n" +
                        "}"));

        HashMap<Integer,Integer> testRuneValues = new HashMap<Integer, Integer>();

        testRuneValues.put(0, 49);
        testRuneValues.put(1, 97);
        testRuneValues.put(2, 228);
        testRuneValues.put(3, 26412);
        testRuneValues.put(4, 9);
        testRuneValues.put(5, 0);
        testRuneValues.put(6, 7);
        testRuneValues.put(7, 255);
        testRuneValues.put(8, 7);
        testRuneValues.put(9, 255);
        testRuneValues.put(10, 4836);
        testRuneValues.put(11, 1053236);
        testRuneValues.put(12, 92);
        testRuneValues.put(13, 39);

        GoVarDeclaration[] declarations =
                childAt(0,
                        file.getGlobalVariables()
                ).getDeclarations();

        GoLiteralString string;

        for (Map.Entry<Integer, Integer> entry : testRuneValues.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            string =
                    getAs(GoLiteralString.class,
                            getAs(GoLiteralExpression.class,
                                    childAt(0,
                                            declarations[key].getExpressions()
                                    )
                            ).getLiteral()
                    );

            assertEquals(GoLiteral.Type.InterpretedString, string.getType());
            assertEquals(GoElementTypes.LITERAL_CHAR, string.getNode().getElementType());
            assertEquals(value, GoPsiUtils.getRuneValue(string.getText()));
        }

        List<String> badRuneValues = Arrays.asList("'aa'", "'\\xa'", "'\\0'", "'\\uDFFF'", "'\\U00110000'");

        for (String rune: badRuneValues){
            assertNull(GoPsiUtils.getRuneValue(rune));
        }

    }
}
