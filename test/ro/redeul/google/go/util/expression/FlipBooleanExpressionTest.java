package ro.redeul.google.go.util.expression;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Assert;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;

public class FlipBooleanExpressionTest extends LightCodeInsightFixtureTestCase {
    public void testNot()           { doTest("!a", "a"); }
    public void testEqual()         { doTest("a == b", "a != b"); }
    public void testLess()          { doTest("a < b", "a >= b"); }
    public void testLessOrEqual()   { doTest("a <= b", "a > b"); }
    public void testGreater()       { doTest("a > b", "a <= b"); }
    public void testGreaterOrEqual(){ doTest("a >= b", "a < b"); }
    public void testOr1()           { doTest("a == b || b == c", "a != b && b != c"); }
    public void testOr2()           { doTest("a == b || b == c || c == d", "a != b && b != c && c != d"); }
    public void testOr3()           { doTest("a == b || (b == c || c == d)", "a != b && (b != c && c != d)"); }
    public void testAnd1()          { doTest("a == b && b == c", "a != b || b != c"); }
    public void testAnd2()          { doTest("a == b && b == c && c == d", "a != b || b != c || c != d"); }
    public void testAnd3()          { doTest("a == b && (b == c && c == d)", "a != b || (b != c || c != d)"); }

    public void testParentheses()   { doTest("(a == b) && (b == c)", "(a != b) || (b != c)"); }

    public void testId()            { doTest("a", "!a"); }
    public void testCall()          { doTest("test()", "!test()"); }

    public void testOther1()        { doTest("test() && a != 5", "!test() || a == 5"); }
    public void testOther2()        { doTest("test() && a < 3 || a > 10", "(!test() || a >= 3) && a <= 10"); }
    public void testOther3()        { doTest("a == 3 || a > 5 && a % 2 == 0", "a != 3 && (a <= 5 || a % 2 != 0)"); }

    private void doTest(String expressionToFlip, String expectedResult) {
        String text = String.format("package main\nvar a=%s", expressionToFlip);
        GoFile file = (GoFile) myFixture.configureByText(GoFileType.INSTANCE, text);
        GoExpr expr = file.getGlobalVariables()[0].getDeclarations()[0].getExpressions()[0];
        Assert.assertEquals(expectedResult, FlipBooleanExpression.flip(expr));
    }
}
