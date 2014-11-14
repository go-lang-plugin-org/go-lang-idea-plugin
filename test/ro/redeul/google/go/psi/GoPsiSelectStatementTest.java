package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClauseDefault;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClauseRecv;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectStatement;

import static ro.redeul.google.go.util.GoPsiTestUtils.*;

public class GoPsiSelectStatementTest extends GoPsiTestCase {

    public void testSimpleSelect() throws Exception {
        GoFile file = get(parse("package main; func main() { select {} }"));

        GoSelectStatement selectStatement =
            castAs(GoSelectStatement.class, 0,
                   get(
                       childAt(0,
                               file.getFunctions()
                       ).getBlock()
                   ).getStatements()
            );

        assertNotNull(selectStatement);
    }

    public void testSelectWithDefault() throws Exception {
        GoFile file = get(parse("" +
                                    "package main; " +
                                    "func main() { " +
                                    "   select {" +
                                    "       default:{}" +
                                    "   } " +
                                    "}"));

        GoSelectStatement selectStatement =
            castAs(GoSelectStatement.class, 0,
                   get(
                       childAt(0,
                               file.getFunctions()
                       ).getBlock()
                   ).getStatements()
            );

        assertEquals(1, selectStatement.getCommClauses().length);

        GoSelectCommClauseDefault commClauseDefault =
            castAs(
                GoSelectCommClauseDefault.class,
                0, selectStatement.getCommClauses());

        assertEquals(commClauseDefault.getText(), "default:{}");
    }

    public void testSelectCommClauseRecv1() throws Exception {
        GoFile file = get(parse("" +
                                    "package main; " +
                                    "func main() {" +
                                    "   select {" +
                                    "       case i1 = <-c1: {}" +
                                    "   } " +
                                    "}"));

        GoSelectStatement selectStatement =
            castAs(GoSelectStatement.class, 0,
                   get(
                       childAt(0,
                               file.getFunctions()
                       ).getBlock()
                   ).getStatements()
            );

        assertEquals(1, selectStatement.getCommClauses().length);

        GoSelectCommClauseRecv clauseRecv =
            castAs(GoSelectCommClauseRecv.class,
                   0, selectStatement.getCommClauses());


        assertNotNull(clauseRecv.getAssignment());
        GoExpr[] expressions = clauseRecv.getAssignment().getLeftSideExpressions().getExpressions();
        assertEquals(1, expressions.length);
        assertEquals("i1", expressions[0].getText());

        assertNull(clauseRecv.getShortVarDeclaration());

        assertEquals("<-c1", get(clauseRecv.getReceiveExpression()).getText());
    }

    public void testSelectCommClauseRecv2() throws Exception {
        GoFile file = get(parse("" +
                                    "package main; " +
                                    "func main() {" +
                                    "   select {" +
                                    "       case i1 := <-c1: {}" +
                                    "   } " +
                                    "}"));

        GoSelectStatement selectStatement =
            castAs(GoSelectStatement.class, 0,
                   get(
                       childAt(0,
                               file.getFunctions()
                       ).getBlock()
                   ).getStatements()
            );

        assertEquals(1, selectStatement.getCommClauses().length);

        GoSelectCommClauseRecv clauseRecv =
            castAs(GoSelectCommClauseRecv.class,
                   0, selectStatement.getCommClauses());

        assertNotNull(clauseRecv.getShortVarDeclaration());

        assertEquals(1, clauseRecv.getShortVarDeclaration().getDeclarations().length);
        assertEquals("i1", childAt(0, clauseRecv.getShortVarDeclaration().getDeclarations()).getText());

        assertNull(clauseRecv.getAssignment());

        assertEquals("<-c1", get(clauseRecv.getReceiveExpression()).getText());
    }
}
