package ro.redeul.google.go.psi;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.util.GoPsiTestUtils;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;

public class GoPsiBlockTest extends GoPsiTestCase {

    public void testListStatements() throws Exception {

        GoFile file = get(parse("" +
                                    "package main;\n" +
                                    "func test() {\n" +
                                    "   v := 5\n" +
                                    "   println(v + 1)\n" +
                                    "}"));

        GoFunctionDeclaration func = GoPsiTestUtils.childAt(0,
                                                            file.getFunctions());
        GoBlockStatement blockStmt = get(func.getBlock());

        assertNotNull(GoPsiTestUtils.castAs(GoShortVarDeclaration.class,
                                            0, blockStmt.getStatements()
        ));

        assertNotNull(GoPsiTestUtils.castAs(GoExpressionStatement.class,
                                            1, blockStmt.getStatements()
        ));
    }

    public void testReturnWithExpressions() throws Exception {

        GoFile file = get(parse("" +
                                    "func Ok4() (int, int) {\n" +
                                    "    return int(1), 1\n" +
                                    "}"));

        GoFunctionDeclaration func = GoPsiTestUtils.childAt(0,
                                                            file.getFunctions());
        GoBlockStatement blockStmt = get(func.getBlock());

        GoReturnStatement statement =
            GoPsiTestUtils.castAs(GoReturnStatement.class,
                                  0, blockStmt.getStatements());

        assertNotNull(GoPsiTestUtils.castAs(GoCallOrConversionExpression.class,
                                            0, statement.getExpressions()
        ));

//        assertNotNull(castAs(statement.getExpressions(), 1,
//                            GoLiteral.class));
    }
}
