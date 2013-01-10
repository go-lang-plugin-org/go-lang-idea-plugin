package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.binary.GoAdditiveExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import static ro.redeul.google.go.util.GoPsiTestUtils.castAs;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;

public class GoPsiStatementsTest extends GoPsiTestCase {

    public void testReturnNothing() throws Exception {

        GoFile file = get(parse("package main; func a() { return }"));
        GoFunctionDeclaration func = childAt(0,
                                             file.getFunctions());
        GoBlockStatement blockStmt = get(func.getBlock());
        GoReturnStatement returnStmt =
            castAs(GoReturnStatement.class,
                   0, blockStmt.getStatements());

        assertTrue(returnStmt.getExpressions().length == 0);
    }

    public void testReturnLiteral() throws Exception {

        GoFile file = get(parse("package main; func a() { return 1}"));
        GoFunctionDeclaration func = childAt(0,
                                             file.getFunctions());
        GoBlockStatement blockStmt = get(func.getBlock());
        GoReturnStatement returnStmt =
            castAs(GoReturnStatement.class,
                   0, blockStmt.getStatements());

        assertNotNull(castAs(GoLiteralExpression.class,
                             0, returnStmt.getExpressions()));
    }

    public void testReturnMultiple() throws Exception {

        GoFile file = get(parse("package main; func a() { return 1,2}"));

        GoFunctionDeclaration func =
            childAt(0, file.getFunctions());

        GoBlockStatement blockStmt = get(func.getBlock());

        GoReturnStatement returnStmt =
            castAs(GoReturnStatement.class, 0, blockStmt.getStatements());

        assertNotNull(
            castAs(GoLiteralExpression.class, 0, returnStmt.getExpressions()));

        assertNotNull(
            castAs(GoLiteralExpression.class, 1, returnStmt.getExpressions()));
    }

    public void testReturnLiteral2() throws Exception {

        GoFile file = get(parse("package main; func a() { return \"a\";\n}"));
        GoFunctionDeclaration func = childAt(0,
                                             file.getFunctions());
        GoBlockStatement blockStmt = get(func.getBlock());
        GoReturnStatement returnStmt =
            castAs(GoReturnStatement.class,
                   0, blockStmt.getStatements());

        assertNotNull(castAs(GoLiteralExpression.class,
                             0, returnStmt.getExpressions()));
    }

    public void testReturnAddExpression() throws Exception {

        GoFile file = get(parse("package main; func a() { return 1+2 }"));
        GoFunctionDeclaration func = childAt(0,
                                             file.getFunctions());
        GoBlockStatement blockStmt = get(func.getBlock());
        GoReturnStatement returnStmt =
            castAs(GoReturnStatement.class,
                   0, blockStmt.getStatements());

        assertNotNull(castAs(GoAdditiveExpression.class,
                             0, returnStmt.getExpressions()));
    }
}
