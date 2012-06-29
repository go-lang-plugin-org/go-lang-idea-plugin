package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import static ro.redeul.google.go.util.GoPsiTestUtils.castAs;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;
import static ro.redeul.google.go.util.GoPsiTestUtils.getAs;

public class GoPsiCallsTest extends GoPsiTestCase {

    public void testNoParams() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "func main() {" +
                      " f()" +
                      "}"));

        GoCallOrConvExpression expr =
            getAs(GoCallOrConvExpression.class,
                  castAs(GoExpressionStatement.class, 0,
                         get(
                             get(
                                 file.getMainFunction()
                             ).getBlock()
                         ).getStatements()
                  ).getExpression()
            );

        assertEquals("f", get(expr.getBaseExpression()).getText());
        assertEquals(0, expr.getArguments().length);
    }

    public void testOneParam() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "func main() {" +
                      " f(1)" +
                      "}"));

        GoCallOrConvExpression expr =
            getAs(GoCallOrConvExpression.class,
                  castAs(GoExpressionStatement.class, 0,
                         get(
                             get(
                                 file.getMainFunction()
                             ).getBlock()
                         ).getStatements()
                  ).getExpression()
            );

        assertEquals("f", get(expr.getBaseExpression()).getText());
        assertEquals(1, expr.getArguments().length);
        assertEquals("1", childAt(0, expr.getArguments()).getText());
    }

    public void testTwoParams() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "func main() {" +
                      " f(1, 2)" +
                      "}"));

        GoCallOrConvExpression expr =
            getAs(GoCallOrConvExpression.class,
                  castAs(GoExpressionStatement.class, 0,
                         get(
                             get(
                                 file.getMainFunction()
                             ).getBlock()
                         ).getStatements()
                  ).getExpression()
            );

        assertEquals("f", get(expr.getBaseExpression()).getText());
        assertEquals(2, expr.getArguments().length);
        assertEquals("1", childAt(0, expr.getArguments()).getText());
        assertEquals("2", childAt(1, expr.getArguments()).getText());
    }

    public void testTwoParamsNil() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "func main() {" +
                      " f(nil, 2)" +
                      "}"));

        GoCallOrConvExpression expr =
            getAs(GoCallOrConvExpression.class,
                  castAs(GoExpressionStatement.class, 0,
                         get(
                             get(
                                 file.getMainFunction()
                             ).getBlock()
                         ).getStatements()
                  ).getExpression()
            );

        assertEquals("f", get(expr.getBaseExpression()).getText());
        assertEquals(2, expr.getArguments().length);
        assertEquals("nil", childAt(0, expr.getArguments()).getText());
        assertEquals("2", childAt(1, expr.getArguments()).getText());
    }

    public void testTwoParamsNilString() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "import \"fmt\"\n" +
                      "func main() {\n" +
                      "     fmt.Fprintf(nil, \"/*begin*/%f/*end.Missing parameter*/\\n\")\n" +
                      "}"));

        GoCallOrConvExpression expr =
            getAs(GoCallOrConvExpression.class,
                  castAs(GoExpressionStatement.class, 0,
                         get(
                             get(
                                 file.getMainFunction()
                             ).getBlock()
                         ).getStatements()
                  ).getExpression()
            );

        assertEquals("fmt.Fprintf", get(expr.getBaseExpression()).getText());
        assertEquals(2, expr.getArguments().length);
        assertEquals("nil", childAt(0, expr.getArguments()).getText());
        assertEquals("\"/*begin*/%f/*end.Missing parameter*/\\n\"", childAt(1, expr.getArguments()).getText());
    }
}
