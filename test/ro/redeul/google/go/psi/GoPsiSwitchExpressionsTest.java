package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchExpressionClause;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchExpressionStatement;
import static ro.redeul.google.go.util.GoPsiTestUtils.castAs;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;

public class GoPsiSwitchExpressionsTest extends GoPsiTestCase {

    public void testDefault() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "func main() int {\n" +
                      "     switch {\n" +
                      "         default:\n" +
                      "             return 1\n" +
                      "     }\n" +
                      "     return nil\n" +
                      "}\n"));

        GoSwitchExpressionStatement exprSwitch =
            castAs(GoSwitchExpressionStatement.class, 0,
                   get(
                       get(
                           file.getMainFunction()
                       ).getBlock()
                   ).getStatements()
            );

        assertNull(exprSwitch.getExpression());
        assertNull(exprSwitch.getSimpleStatement());
        assertEquals(1, exprSwitch.getClauses().length);

        GoSwitchExpressionClause clause =
            childAt(0, exprSwitch.getClauses());

        assertEquals(true, clause.isDefault());

        assertEquals(1, clause.getStatements().length);

        assertEquals("return 1", castAs(GoReturnStatement.class, 0, clause.getStatements()).getText());
    }

    public void testWithStatement() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "func main() int {\n" +
                      "     switch x := 1; x {\n" +
                      "         default:\n" +
                      "             return x\n" +
                      "     }\n" +
                      "     return nil\n" +
                      "}\n"));

        GoSwitchExpressionStatement exprSwitch =
            castAs(GoSwitchExpressionStatement.class, 0,
                   get(
                       get(
                           file.getMainFunction()
                       ).getBlock()
                   ).getStatements()
            );

        assertEquals("x := 1", get(exprSwitch.getSimpleStatement()).getText());
        assertEquals("x", get(exprSwitch.getExpression()).getText());
    }

    public void testClauses() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "func main() int {\n" +
                      "     switch x := 1; x {\n" +
                      "         case 1:\n" +
                      "             a := 1\n" +
                      "             return a\n" +
                      "         default:\n" +
                      "             return x\n" +
                      "     }\n" +
                      "     return nil\n" +
                      "}\n"));

        GoSwitchExpressionStatement exprSwitch =
            castAs(GoSwitchExpressionStatement.class, 0,
                   get(
                       get(
                           file.getMainFunction()
                       ).getBlock()
                   ).getStatements()
            );

        assertEquals("x := 1", get(exprSwitch.getSimpleStatement()).getText());
        assertEquals("x", get(exprSwitch.getExpression()).getText());

        GoSwitchExpressionClause clause = childAt(0, exprSwitch.getClauses());

        assertEquals(1, clause.getExpressions().length);
        assertEquals("1", childAt(0, clause.getExpressions()).getText());
        assertFalse(clause.isDefault());

        clause = childAt(1, exprSwitch.getClauses());

        assertEquals(0, clause.getExpressions().length);
        assertTrue(clause.isDefault());
    }

    public void testClauseWithExpressionList() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "func main() int {\n" +
                      "     switch {\n" +
                      "         case 1, 2, 4:\n" +
                      "             a := 1\n" +
                      "             return a\n" +
                      "     }\n" +
                      "     return nil\n" +
                      "}\n"));

        GoSwitchExpressionStatement exprSwitch =
            castAs(GoSwitchExpressionStatement.class, 0,
                   get(
                       get(
                           file.getMainFunction()
                       ).getBlock()
                   ).getStatements()
            );

        assertNull(exprSwitch.getSimpleStatement());
        assertNull(exprSwitch.getExpression());

        GoSwitchExpressionClause clause = childAt(0, exprSwitch.getClauses());

        assertEquals(3, clause.getExpressions().length);
        assertEquals("1", childAt(0, clause.getExpressions()).getText());
        assertEquals("2", childAt(1, clause.getExpressions()).getText());
        assertEquals("4", childAt(2, clause.getExpressions()).getText());
        assertFalse(clause.isDefault());
    }
}
