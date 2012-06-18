package ro.redeul.google.go.psi;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSliceExpression;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import static ro.redeul.google.go.util.GoPsiTestUtils.castAs;
import static ro.redeul.google.go.util.GoPsiTestUtils.childAt;
import static ro.redeul.google.go.util.GoPsiTestUtils.get;
import static ro.redeul.google.go.util.GoPsiTestUtils.getAs;

public class GoPsiSliceExpressionsTestCase extends GoPsiTestCase {

    public void testNormalSlice() throws Exception {
        GoFile file = get(parse("package main; func a() { a[i:j] }"));


        GoSliceExpression sliceExpression =
            getAs(GoSliceExpression.class,
                  castAs(GoExpressionStatement.class, 0,
                         get(
                             childAt(0,
                                     file.getFunctions()
                             ).getBlock()
                         ).getStatements()
                  ).getExpression()
            );

        assertEquals("a", get(sliceExpression.getBaseExpression()).getText());
        assertEquals("i", get(sliceExpression.getFirstIndex()).getText());
        assertEquals("j", get(sliceExpression.getSecondIndex()).getText());
    }

    public void testEmptySlice() throws Exception {
        GoFile file = get(parse("package main; func a() { a[:] }"));


        GoSliceExpression sliceExpression =
            getAs(GoSliceExpression.class,
                  castAs(GoExpressionStatement.class, 0,
                         get(
                             childAt(0,
                                     file.getFunctions()
                             ).getBlock()
                         ).getStatements()
                  ).getExpression()
            );

        assertEquals("a", get(sliceExpression.getBaseExpression()).getText());
        assertNull(sliceExpression.getFirstIndex());
        assertNull(sliceExpression.getSecondIndex());
    }

    public void testSliceFirstIndex() throws Exception {
        GoFile file = get(parse("package main; func a() { a[i:] }"));


        GoSliceExpression sliceExpression =
            getAs(GoSliceExpression.class,
                  castAs(GoExpressionStatement.class, 0,
                         get(
                             childAt(0,
                                     file.getFunctions()
                             ).getBlock()
                         ).getStatements()
                  ).getExpression()
            );

        assertEquals("a", get(sliceExpression.getBaseExpression()).getText());
        assertEquals("i", get(sliceExpression.getFirstIndex()).getText());
        assertNull(sliceExpression.getSecondIndex());
    }

    public void testSliceSecondIndex() throws Exception {
        GoFile file = get(parse("package main; func a() { a[:j] }"));


        GoSliceExpression sliceExpression =
            getAs(GoSliceExpression.class,
                  castAs(GoExpressionStatement.class, 0,
                         get(
                             childAt(0,
                                     file.getFunctions()
                             ).getBlock()
                         ).getStatements()
                  ).getExpression()
            );

        assertEquals("a", get(sliceExpression.getBaseExpression()).getText());
        assertNull(sliceExpression.getFirstIndex());
        assertEquals("j", get(sliceExpression.getSecondIndex()).getText());
    }

    public void testSliceWithCommentsAndWhitespaces() throws Exception {
        GoFile file = get(parse("" +
                                    "package main; func a() { " +
                                    "   ad[\n" +
                                    "/**/ 1/**/:/**/2/**/] }\n" +
                                    ""));

        GoSliceExpression sliceExpression =
            getAs(GoSliceExpression.class,
                  castAs(GoExpressionStatement.class, 0,
                         get(
                             childAt(0,
                                     file.getFunctions()
                             ).getBlock()
                         ).getStatements()
                  ).getExpression()
            );

        assertEquals("ad", get(sliceExpression.getBaseExpression()).getText());
        assertEquals("1", get(sliceExpression.getFirstIndex()).getText());
        assertEquals("2", get(sliceExpression.getSecondIndex()).getText());
    }
}
