package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoAdditiveExpression;
import ro.redeul.google.go.lang.psi.statements.GoForWithClausesStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeAndVarsStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeStatement;
import static ro.redeul.google.go.util.GoPsiTestUtils.castAs;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;
import static ro.redeul.google.go.util.GoPsiTestUtils.getAs;

public class GoPsiForStatementTest extends GoPsiTestCase {

    public void testForWithClauseNullCondition() throws Exception {
        GoFile file = get(parse("" +
                                    "package main;\n" +
                                    "func test() {\n" +
                                    "   for ;; {}\n" +
                                    "}"));

        GoForWithClausesStatement forStmt =
            castAs(GoForWithClausesStatement.class, 0,
                   get(
                       childAt(0,
                               file.getFunctions()
                       ).getBlock()
                   ).getStatements()
            );

        assertNull(forStmt.getCondition());
    }

    public void testForWithClauseNotNullCondition() throws Exception {
        GoFile file = get(parse("" +
                                    "package main;\n" +
                                    "func test() {\n" +
                                    "   for ;e; {}\n" +
                                    "}"));

        GoForWithClausesStatement forStmt =
            castAs(GoForWithClausesStatement.class, 0,
                   get(
                       childAt(0,
                               file.getFunctions()
                       ).getBlock()
                   ).getStatements()
            );

        getAs(GoLiteralExpression.class, forStmt.getCondition());
    }

    public void testForWithClauseNotNullConditionAdditive() throws Exception {
        GoFile file = get(parse("" +
                                    "package main;\n" +
                                    "func test() {\n" +
                                    "   for ;e+f; {}\n" +
                                    "}"));

        GoForWithClausesStatement forStmt =
            castAs(GoForWithClausesStatement.class, 0,
                   get(
                       childAt(0,
                               file.getFunctions()
                       ).getBlock()
                   ).getStatements()
            );

        getAs(GoAdditiveExpression.class, forStmt.getCondition());
    }

    public void testForWithRangeKeyNotNullConditionAdditive() throws Exception {
        GoFile file = get(parse("" +
                                    "package main;\n" +
                                    "func main() {\n" +
                                    "    for i := range data {\n" +
                                    "        println(i)\n" +
                                    "    }\n" +
                                    "}"));

        GoForWithRangeAndVarsStatement forStmt =
            castAs(GoForWithRangeAndVarsStatement.class, 0,
                   get(
                       childAt(0,
                               file.getFunctions()
                       ).getBlock()
                   ).getStatements()
            );

        assertEquals("i", get(forStmt.getKey()).getText());
        assertNull(forStmt.getValue());
        assertEquals("data", get(forStmt.getRangeExpression()).getText());
    }

    public void testForWithRangeKeyValueNoRange() throws Exception {
        GoFile file = get(parse("" +
                                    "package main;\n" +
                                    "func main() {\n" +
                                    "    for key, value := range {\n" +
                                    "        println(i)\n" +
                                    "    }\n" +
                                    "}"));

        GoForWithRangeAndVarsStatement forStmt =
            castAs(GoForWithRangeAndVarsStatement.class, 0,
                   get(
                       childAt(0,
                               file.getFunctions()
                       ).getBlock()
                   ).getStatements()
            );

        assertEquals("key", get(forStmt.getKey()).getText());
        assertEquals("value", get(forStmt.getValue()).getText());
        assertNull(forStmt.getRangeExpression());
    }

    public void testForWithRange() throws Exception {
        GoFile file = get(parse("" +
                                    "package main;\n" +
                                    "func main() {\n" +
                                    "    for key, value := range data {\n" +
                                    "        println(i)\n" +
                                    "    }\n" +
                                    "}"));

        GoForWithRangeAndVarsStatement forStmt =
            castAs(GoForWithRangeAndVarsStatement.class, 0,
                   get(
                       childAt(0,
                               file.getFunctions()
                       ).getBlock()
                   ).getStatements()
            );

        assertEquals("key", get(forStmt.getKey()).getText());
        assertEquals("value", get(forStmt.getValue()).getText());
        assertEquals("data", get(forStmt.getRangeExpression()).getText());
    }

    public void testForWithRange2() throws Exception {
        GoFile file = get(parse("" +
                                    "package main;\n" +
                                    "func main() {\n" +
                                    "    for key, value = range data {\n" +
                                    "        println(i)\n" +
                                    "    }\n" +
                                    "}"));

        GoForWithRangeStatement forStmt =
            castAs(GoForWithRangeStatement.class, 0,
                   get(
                       childAt(0,
                               file.getFunctions()
                       ).getBlock()
                   ).getStatements()
            );

        assertEquals("key", get(forStmt.getKey()).getText());
        assertEquals("value", get(forStmt.getValue()).getText());
        assertEquals("data", get(forStmt.getRangeExpression()).getText());
    }
}
