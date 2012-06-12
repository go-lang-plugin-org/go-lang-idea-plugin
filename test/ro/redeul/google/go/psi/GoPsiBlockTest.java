package ro.redeul.google.go.psi;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;
import static ro.redeul.google.go.util.GoPsiTestUtils.getAs;

public class GoPsiBlockTest extends GoPsiTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void testListStatements() throws Exception {

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

    public void testReturnWithExpressions() throws Exception {

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
