package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeClause;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeGuard;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeStatement;
import static ro.redeul.google.go.util.GoPsiTestUtils.castAs;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;

public class GoPsiSwitchTypesTest extends GoPsiTestCase {

    public void testDefault() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "func main() int {\n" +
                      "     switch x.(type) {\n" +
                      "         default:\n" +
                      "             return 1\n" +
                      "     }\n" +
                      "     return nil\n" +
                      "}\n"));

        GoSwitchTypeStatement typeSwitch =
            castAs(GoSwitchTypeStatement.class, 0,
                   get(
                       get(
                           file.getMainFunction()
                       ).getBlock()
                   ).getStatements()
            );

        assertNull(typeSwitch.getSimpleStatement());

        GoSwitchTypeGuard typeGuard = get(typeSwitch.getTypeGuard());

        assertNull(typeGuard.getIdentifier());
        assertEquals("x", get(typeGuard.getExpression()).getText());

        GoSwitchTypeClause clause = childAt(0, typeSwitch.getClauses());

        assertEquals(true, clause.isDefault());

        assertEquals(1, clause.getStatements().length);

        assertEquals("return 1", castAs(GoReturnStatement.class, 0,
                                        clause.getStatements()).getText());
    }

    public void testWithStatement() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "func main() int {\n" +
                      "     switch x := 1; x.(type) {\n" +
                      "         default:\n" +
                      "             return x\n" +
                      "     }\n" +
                      "     return nil\n" +
                      "}\n"));

        GoSwitchTypeStatement exprSwitch =
            castAs(GoSwitchTypeStatement.class, 0,
                   get(
                       get(
                           file.getMainFunction()
                       ).getBlock()
                   ).getStatements()
            );

        assertEquals("x := 1", get(exprSwitch.getSimpleStatement()).getText());
        assertEquals("x",
                     get(exprSwitch.getTypeGuard().getExpression()).getText());
    }

    public void testClauses() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "func main() int {\n" +
                      "     switch x.(type) {\n" +
                      "         case int, float, T:\n" +
                      "             a := 1\n" +
                      "             return a\n" +
                      "         case complex32:\n" +
                      "             return x\n" +
                      "     }\n" +
                      "     return nil\n" +
                      "}\n"));

        GoSwitchTypeStatement exprSwitch =
            castAs(GoSwitchTypeStatement.class, 0,
                   get(
                       get(
                           file.getMainFunction()
                       ).getBlock()
                   ).getStatements()
            );

        assertNull(exprSwitch.getSimpleStatement());
        assertEquals("x",
                     get(
                         get(
                             exprSwitch.getTypeGuard()
                         ).getExpression()
                     ).getText());

        GoSwitchTypeClause clause = childAt(0, exprSwitch.getClauses());

        assertEquals(3, clause.getTypes().length);
        assertEquals("int", childAt(0, clause.getTypes()).getText());
        assertEquals("float", childAt(1, clause.getTypes()).getText());
        assertEquals("T", childAt(2, clause.getTypes()).getText());
        assertFalse(clause.isDefault());

        clause = childAt(1, exprSwitch.getClauses());

        assertEquals(1, clause.getTypes().length);
        assertEquals("complex32", childAt(0, clause.getTypes()).getText());
    }
}
