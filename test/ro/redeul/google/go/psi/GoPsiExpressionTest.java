package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.inspection.FunctionCallInspection;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;

import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;

public class GoPsiExpressionTest extends GoPsiTestCase {

    private static final float DELTA = (float) 1e-5;

    public void testGetNumberValueFromExpression() throws Exception {
        GoFile file = get(
                parse("" +
                        "package main\n" +
                        "const (\n" +
                        "     x0 = 100\n" +
                        "     x1 = 101.0\n" +
                        "     x2 = 102.3456\n" +
                        "     x3 = 'g'\n" +
                        "     x4 = -104\n" +
                        "     x5 = 50 + 55\n" +
                        "     x6 = (206 - (50 + 50))\n" +
                        "     x7 = 5 * 10 * 10 / 5 + 7\n" +
                        "     x8 = 8 / 0\n" +
                        "     x9 = 9.0 / 0.0\n" +
                        "     x10 = 2213 / 20\n" +
                        "     x11 = 2233 / 20.\n" +
                        "     x12 = (50 + 50.) * 2 - (265 / 3) \n" +
                        "     x13 = 1 << 3.0 + 1.0 << 3\n" +
                        "     x14 = 1.2 << 3\n" +
                        "     x15 = 1 << 3.1\n" +
                        "     x16 = ^-17\n" +
                        "     x17 = ^-18.0\n" +

                        ")"));

        GoConstDeclaration[] declarations =
                childAt(0,
                        file.getConsts()
                ).getDeclarations();

        Number val;

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[0].getExpressions()));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, 100);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[1].getExpressions()));
        assertInstanceOf(val, Float.class);
        assertEquals((Float) val, 101.0, DELTA);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[2].getExpressions()));
        assertInstanceOf(val, Float.class);
        assertEquals((Float) val, 102.3456, DELTA);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[3].getExpressions()));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, 103);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[4].getExpressions()));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, -104);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[5].getExpressions()));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, 105);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[6].getExpressions()));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, 106);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[7].getExpressions()));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, 107);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[8].getExpressions()));
        assertNull(val);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[9].getExpressions()));
        assertNull(val);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[10].getExpressions()));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, 110);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[11].getExpressions()));
        assertInstanceOf(val, Float.class);
        assertEquals((Float) val, 111.65, DELTA);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[12].getExpressions()));
        assertInstanceOf(val, Float.class);
        assertEquals((Float) val, 112, DELTA);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[13].getExpressions()));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, 16);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[14].getExpressions()));
        assertNull(val);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[15].getExpressions()));
        assertNull(val);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[16].getExpressions()));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, 16);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[17].getExpressions()));
        assertNull(val);

    }

    public void testGetNumberValueFromIotaExpression() throws Exception {
        GoFile file = get(
                parse("" +
                        "package main\n" +
                        "const (\n" +
                        "     x01, x02 = iota, iota * 2\n" +
                        "     x11, x12\n" +
                        "     x21, x22 = x11 + 2, x12 + 3\n" +
                        "     x31, x32 = (iota * 2.0), -iota\n" +

                        ")"));

        GoConstDeclaration[] declarations =
                childAt(0,
                        file.getConsts()
                ).getDeclarations();

        Number val;

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(declarations[0].getExpression(declarations[0].getIdentifiers()[0]));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, 0);
        val = FunctionCallInspection.getNumberValueFromLiteralExpr(declarations[0].getExpression(declarations[0].getIdentifiers()[1]));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, 0);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(declarations[1].getExpression(declarations[1].getIdentifiers()[0]));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, 1);
        val = FunctionCallInspection.getNumberValueFromLiteralExpr(declarations[1].getExpression(declarations[1].getIdentifiers()[1]));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, 2);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(declarations[2].getExpression(declarations[2].getIdentifiers()[0]));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, 3);
        val = FunctionCallInspection.getNumberValueFromLiteralExpr(declarations[2].getExpression(declarations[2].getIdentifiers()[1]));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, 5);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(declarations[3].getExpression(declarations[3].getIdentifiers()[0]));
        assertInstanceOf(val, Float.class);
        assertEquals((Float) val, 6.0, DELTA);
        val = FunctionCallInspection.getNumberValueFromLiteralExpr(declarations[3].getExpression(declarations[3].getIdentifiers()[1]));
        assertInstanceOf(val, Integer.class);
        assertEquals(val, -3);

    }

}
