package ro.redeul.google.go.lang.psi;

import ro.redeul.google.go.lang.psi.expressions.GoCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

public class GoPsiBlockTestCase extends AbstractGoPsiTestCase {

    public void testListStatements() {

        GoFile file = get(parse("" +
                                    "package main;\n" +
                                    "func test() {\n" +
                                    "   v := 5\n" +
                                    "   println(v + 1)\n" +
                                    "}"));

        GoFunctionDeclaration func = get(file.getFunctions(), 0);
        GoBlockStatement blockStmt = get(func.getBlock());

        assertNotNull(getAs(blockStmt.getStatements(), 0,
                            GoShortVarDeclaration.class));

        assertNotNull(getAs(blockStmt.getStatements(), 1,
                            GoExpressionStatement.class));
    }

    public void testReturnWithExpressions() {

        GoFile file = get(parse("" +
                                    "func Ok4() (int, int) {\n" +
                                    "    return int(1), 1\n" +
                                    "}"));

        GoFunctionDeclaration func = get(file.getFunctions(), 0);
        GoBlockStatement blockStmt = get(func.getBlock());

        GoReturnStatement statement =
            getAs(blockStmt.getStatements(), 0, GoReturnStatement.class);

        assertNotNull(getAs(statement.getExpressions(), 0,
                            GoCallOrConversionExpression.class));

//        assertNotNull(getAs(statement.getExpressions(), 1,
//                            GoLiteral.class));
    }
}
