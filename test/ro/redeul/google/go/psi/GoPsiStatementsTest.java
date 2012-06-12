package ro.redeul.google.go.psi;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoAdditiveExpression;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;
import static ro.redeul.google.go.util.GoPsiTestUtils.getAs;

public class GoPsiStatementsTest extends GoPsiTestCase {

    public void testReturnNothing() throws Exception {

        GoFile file = get(parse("package main; func a() { return }"));
        GoFunctionDeclaration func = get(file.getFunctions(), 0);
        GoBlockStatement blockStmt = get(func.getBlock());
        GoReturnStatement returnStmt =
            getAs(blockStmt.getStatements(), 0, GoReturnStatement.class);

        assertTrue(returnStmt.getExpressions().length == 0);
    }

    public void testReturnLiteral() throws Exception {

        GoFile file = get(parse("package main; func a() { return 1}"));
        GoFunctionDeclaration func = get(file.getFunctions(), 0);
        GoBlockStatement blockStmt = get(func.getBlock());
        GoReturnStatement returnStmt =
            getAs(blockStmt.getStatements(), 0, GoReturnStatement.class);

        assertNotNull(getAs(returnStmt.getExpressions(), 0, GoLiteralExpression.class));
    }

    public void testReturnMultiple() throws Exception {

        GoFile file = get(parse("package main; func a() { return 1,2}"));
        GoFunctionDeclaration func = get(file.getFunctions(), 0);
        GoBlockStatement blockStmt = get(func.getBlock());
        GoReturnStatement returnStmt =
            getAs(blockStmt.getStatements(), 0, GoReturnStatement.class);

        assertNotNull(getAs(returnStmt.getExpressions(), 0, GoLiteralExpression.class));
        assertNotNull(getAs(returnStmt.getExpressions(), 1, GoLiteralExpression.class));
    }

    public void testReturnLiteral2() throws Exception {

        GoFile file = get(parse("package main; func a() { return \"a\";\n}"));
        GoFunctionDeclaration func = get(file.getFunctions(), 0);
        GoBlockStatement blockStmt = get(func.getBlock());
        GoReturnStatement returnStmt =
            getAs(blockStmt.getStatements(), 0, GoReturnStatement.class);

        assertNotNull(getAs(returnStmt.getExpressions(), 0, GoLiteralExpression.class));
    }

    public void testReturnAddExpression() throws Exception {

        GoFile file = get(parse("package main; func a() { return 1+2 }"));
        GoFunctionDeclaration func = get(file.getFunctions(), 0);
        GoBlockStatement blockStmt = get(func.getBlock());
        GoReturnStatement returnStmt =
            getAs(blockStmt.getStatements(), 0, GoReturnStatement.class);

        assertNotNull(getAs(returnStmt.getExpressions(), 0, GoAdditiveExpression.class));
    }
}
