package ro.redeul.google.go.psi;

import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.primary.GoTypeAssertionExpression;
import ro.redeul.google.go.lang.psi.types.GoTypeSlice;
import static ro.redeul.google.go.util.GoPsiTestUtils.castAs;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;
import static ro.redeul.google.go.util.GoPsiTestUtils.getAs;

public class GoPsiTypeAssertionsTest extends GoPsiTestCase {


    public void testBasic() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "var e = i.(int)"));

        GoTypeAssertionExpression typeAssertion =
            castAs(GoTypeAssertionExpression.class, 0,
                   childAt(0,
                           childAt(0,
                                   file.getGlobalVariables()
                           ).getDeclarations()
                   ).getExpressions()
            );

        assertEquals("i", get(typeAssertion.getBaseExpression()).getText());
        assertEquals("int", get(typeAssertion.getAssertedType()).getText());
    }

    public void testArray() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "var e = i.([]int)"));

        GoTypeAssertionExpression typeAssertion =
            castAs(GoTypeAssertionExpression.class, 0,
                   childAt(0,
                           childAt(0,
                                   file.getGlobalVariables()
                           ).getDeclarations()
                   ).getExpressions()
            );

        assertEquals("i", get(typeAssertion.getBaseExpression()).getText());
        assertEquals("[]int", get(typeAssertion.getAssertedType()).getText());
        assertEquals("int",
                     get(
                         getAs(GoTypeSlice.class,
                               typeAssertion.getAssertedType()
                         ).getElementType()
                     ).getText());



    }
}
