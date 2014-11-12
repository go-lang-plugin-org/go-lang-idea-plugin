package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.inspection.FunctionCallInspection;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;

import java.math.BigDecimal;
import java.math.BigInteger;

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

        Number val = null;

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[0].getExpressions()));
        assertInstanceOf(val, BigInteger.class);
        assertEquals(val.intValue(), 100);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[1].getExpressions()));
        assertInstanceOf(val, BigDecimal.class);
        assertEquals(val.floatValue(), 101.0, DELTA);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[2].getExpressions()));
        assertInstanceOf(val, BigDecimal.class);
        assertEquals(val.floatValue(), 102.3456, DELTA);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[3].getExpressions()));
        assertInstanceOf(val, BigInteger.class);
        assertEquals(val.intValue(), 103);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[4].getExpressions()));
        assertInstanceOf(val, BigInteger.class);
        assertEquals(val.intValue(), -104);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[5].getExpressions()));
        assertInstanceOf(val, BigInteger.class);
        assertEquals(105, val.intValue());

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[6].getExpressions()));
        assertInstanceOf(val, BigInteger.class);
        assertEquals(106, val.intValue());

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[7].getExpressions()));
        assertInstanceOf(val, BigInteger.class);
        assertEquals(107, val.intValue());

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[8].getExpressions()));
        assertNull(val);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[9].getExpressions()));
        assertNull(val);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[10].getExpressions()));
        assertInstanceOf(val, BigInteger.class);
        assertEquals(110, val.intValue());

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[11].getExpressions()));
        assertInstanceOf(val, BigDecimal.class);
        assertEquals(111.65, val.floatValue(),DELTA);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[12].getExpressions()));
        assertInstanceOf(val, BigDecimal.class);
        assertEquals(112, val.floatValue(), DELTA);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[13].getExpressions()));
        assertInstanceOf(val, BigInteger.class);
        assertEquals(16, val.intValue());

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[14].getExpressions()));
        assertNull(val);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[15].getExpressions()));
        assertNull(val);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(childAt(0, declarations[16].getExpressions()));
        assertInstanceOf(val, BigInteger.class);
        assertEquals(val.intValue(), 16);

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
        assertNotNull(val);
        assertInstanceOf(val, BigInteger.class);
        assertEquals(BigInteger.ZERO, val);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(declarations[0].getExpression(declarations[0].getIdentifiers()[1]));
        assertNotNull(val);
        assertInstanceOf(val, BigInteger.class);
        assertEquals(0, val.intValue());

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(declarations[1].getExpression(declarations[1].getIdentifiers()[0]));
        assertNotNull(val);
        assertInstanceOf(val, BigInteger.class);
        assertEquals(1, val.intValue());

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(declarations[1].getExpression(declarations[1].getIdentifiers()[1]));
        assertNotNull(val);
        assertInstanceOf(val, BigInteger.class);
        assertEquals(2, val.intValue());

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(declarations[2].getExpression(declarations[2].getIdentifiers()[0]));
        assertNotNull(val);
        assertInstanceOf(val, BigInteger.class);
        assertEquals(3, val.intValue());

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(declarations[2].getExpression(declarations[2].getIdentifiers()[1]));
        assertNotNull(val);
        assertInstanceOf(val, BigInteger.class);
        assertEquals(5, val.intValue());

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(declarations[3].getExpression(declarations[3].getIdentifiers()[0]));
        assertNotNull(val);
        assertInstanceOf(val, BigDecimal.class);
        assertEquals(6.0, val.floatValue(), DELTA);

        val = FunctionCallInspection.getNumberValueFromLiteralExpr(declarations[3].getExpression(declarations[3].getIdentifiers()[1]));
        assertNotNull(val);
        assertInstanceOf(val, BigInteger.class);
        assertEquals(-3, val.intValue());
    }
}
