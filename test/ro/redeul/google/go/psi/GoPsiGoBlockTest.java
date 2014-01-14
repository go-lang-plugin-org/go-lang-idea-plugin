package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import static ro.redeul.google.go.util.GoPsiTestUtils.castAs;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;

public class GoPsiGoBlockTest extends GoPsiTestCase {

    public void testListStatements() throws Exception {

        GoFile file = get(parse("" +
                                    "package main;\n" +
                                    "func test() {\n" +
                                    "   v := 5\n" +
                                    "   println(v + 1)\n" +
                                    "}"));

        GoBlockStatement block =
            get(
                childAt(0,
                        file.getFunctions()
                ).getBlock()
            );

        castAs(GoShortVarDeclaration.class, 0, block.getStatements());
        castAs(GoExpressionStatement.class, 1, block.getStatements());
    }

    public void testReturnWithExpressions() throws Exception {

        GoFile file = get(parse("" +
                                    "package main\n" +
                                    "func Ok4() (int, int) {\n" +
                                    "    return int(1), 1\n" +
                                    "}"));

        GoReturnStatement statement =
            castAs(GoReturnStatement.class, 0,
                   get(
                       childAt(0,
                               file.getFunctions()
                       ).getBlock()
                   ).getStatements()
            );

        castAs(GoCallOrConvExpression.class, 0, statement.getExpressions());
    }
}
