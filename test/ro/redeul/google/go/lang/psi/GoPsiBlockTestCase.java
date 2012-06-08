package ro.redeul.google.go.lang.psi;

import ro.redeul.google.go.lang.psi.declarations.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
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
}
