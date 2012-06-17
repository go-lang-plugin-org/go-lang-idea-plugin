package ro.redeul.google.go.psi;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralInteger;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralComposite;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeValue;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import static ro.redeul.google.go.util.GoPsiTestUtils.castAs;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;
import static ro.redeul.google.go.util.GoPsiTestUtils.getAs;

public class GoPsiCompositeLiteralsTest extends GoPsiTestCase {


    public void testKey() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "var e = Line{key:1, 2:1)"));

        GoLiteralCompositeValue value =
            get(
                getAs(GoLiteralComposite.class,
                      castAs(GoLiteralExpression.class, 0,
                             childAt(0,
                                     childAt(0,
                                             file.getGlobalVariables()
                                     ).getDeclarations()
                             ).getExpressions()
                      ).getLiteral()
                ).getValue());

        GoLiteralCompositeElement element;
        element = childAt(0, value.getElements());

        assertEquals("key", get(element.getKey()).getName());
        get(element.getIndex());
        assertNull(element.getLiteralValue());
        assertEquals("1",
                     getAs(GoLiteralInteger.class,
                           getAs(GoLiteralExpression.class,
                                 element.getExpressionValue()
                           ).getLiteral()
                     ).getText());

        element = childAt(1, value.getElements());

        assertNull(element.getKey());
        assertEquals("2",
                     getAs(GoLiteralInteger.class,
                           getAs(GoLiteralExpression.class,
                                 element.getIndex()
                           ).getLiteral()
                     ).getText());
        assertNull(element.getLiteralValue());
        assertEquals("1",
                     getAs(GoLiteralInteger.class,
                           getAs(GoLiteralExpression.class,
                                 element.getExpressionValue()
                           ).getLiteral()
                     ).getText());
    }

    public void testNestedLiteral() throws Exception {
        GoFile file = get(
            parse("" +
                      "package main\n" +
                      "var e = Line{origin, Point{y: -4, z: 12.3}}\n"));


        GoLiteralComposite composite =
            getAs(GoLiteralComposite.class,
                  castAs(GoLiteralExpression.class, 0,
                         childAt(0,
                                 childAt(0,
                                         file.getGlobalVariables()
                                 ).getDeclarations()
                         ).getExpressions()
                  ).getLiteral());

        assertEquals("Line",
                     getAs(GoTypeName.class,
                           composite.getLiteralType()
                     ).getName());

        GoLiteralCompositeValue value = get(composite.getValue());

        GoLiteralCompositeElement valueElement;
        valueElement = childAt(0, value.getElements());

        assertNull(valueElement.getKey());
        assertNull(valueElement.getIndex());
        assertNull(valueElement.getLiteralValue());

        assertEquals("origin",
                     getAs(GoLiteralIdentifier.class,
                           getAs(GoLiteralExpression.class,
                                 valueElement.getExpressionValue()
                           ).getLiteral()
                     ).getName());

        valueElement = childAt(1, value.getElements());
        assertNull(valueElement.getKey());
        assertNull(valueElement.getIndex());
        assertNull(valueElement.getLiteralValue());
        getAs(GoLiteralComposite.class,
              getAs(GoLiteralExpression.class,
                    valueElement.getExpressionValue()).getLiteral());
    }
}
