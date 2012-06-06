package ro.redeul.google.go.lang.parser;

import java.io.File;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Aug 5, 2010
 */
public class GoExpressionsTestCase extends GoParsingTestCase {

    @Override
    protected String getBasePath() {
        return super.getBasePath() + File.separator + "expressions";
    }

    public void testCall_or_conversion$parameter() throws Throwable {
        doTest();
    }

    public void testCall_or_conversion$simple() throws Throwable {
        doTest();
    }

    public void testCall_or_conversion$parameters() throws Throwable {
        doTest();
    }

    public void testCall_or_conversion$parenthesised() throws Throwable {
        doTest();
    }

    public void testIndex$simple() throws Throwable {
        doTest();
    }

    public void testIndex$expression() throws Throwable {
        doTest();
    }

    public void testSlice$simple() throws Throwable {
        doTest();
    }

    public void testSlice$expressions() throws Throwable {
        doTest();
    }

    public void testType_assertion$simple() throws Throwable {
        doTest();
    }

    public void testType_assertion$type() throws Throwable {
        doTest();
    }

    public void testSelector$simple() throws Throwable {
        doTest();
    }

    public void testSelector$double() throws Throwable {
        doTest();
    }

    public void testParenthesised$basic() throws Throwable {
        doTest();
    }

//    @Test public void testParenthesised$Pointer_type() throws Throwable { doTest(); }

    public void testLiteral$identifier_qualified() throws Throwable {
        doTest();
    }

    public void testLiteral$identifier() throws Throwable {
        doTest();
    }

    public void testLiteral$integer() throws Throwable {
        doTest();
    }

    public void testLiteral$float() throws Throwable {
        doTest();
    }

    public void testLiteral$imaginary() throws Throwable {
        doTest();
    }

    public void testLiteral$char() throws Throwable {
        doTest();
    }

    public void testLiteral$string() throws Throwable {
        doTest();
    }

    public void testLiteral$composite$error1() throws Throwable {
        doTest();
    }

    public void testLiteral$composite$builtins() throws Throwable {
        doTest();
    }

    public void testLiteral$composite$struct() throws Throwable {
        doTest();
    }

    public void testLiteral$composite$struct_nested() throws Throwable {
        doTest();
    }

    public void testLiteral$composite$slice() throws Throwable {
        doTest();
    }

    public void testLiteral$composite$array() throws Throwable {
        doTest();
    }

    public void testLiteral$composite$array_implicit_length() throws Throwable {
        doTest();
    }

    public void testLiteral$composite$map() throws Throwable {
        doTest();
    }

    public void testLiteral$composite$type() throws Throwable {
        doTest();
    }

    public void testLiteral$composite$type2() throws Throwable {
        doTest();
    }

    public void testLiteral$function1() throws Throwable {
        doTest();
    }

    public void testLiteral$function2() throws Throwable {
        doTest();
    }

    public void testPrimary$primary1() throws Throwable {
        doTest();
    }

    public void testPrimary$primary2() throws Throwable {
        doTest();
    }

    public void testPrimary$primary3() throws Throwable {
        doTest();
    }

    public void testPrimary$primary4() throws Throwable {
        doTest();
    }

    public void testPrimary$primary5() throws Throwable {
        doTest();
    }

    public void testPrimary$primary6() throws Throwable {
        doTest();
    }

    public void testPrimary$primary7() throws Throwable {
        doTest();
    }

    public void testPrimary$primary7_qualified() throws Throwable {
        doTest();
    }

    public void testPrimary$primary8() throws Throwable {
        doTest();
    }

    public void testPrimary$primary9() throws Throwable {
        doTest();
    }

    public void testBinary$simple$additive() throws Throwable {
        doTest();
    }

    public void testBinary$simple$substraction() throws Throwable {
        doTest();
    }

    public void testBinary$simple$multiplicative() throws Throwable {
        doTest();
    }

    public void testBinary$simple$relational() throws Throwable {
        doTest();
    }

    public void testBinary$simple$communication() throws Throwable {
        doTest();
    }

    public void testBinary$simple$logical_and() throws Throwable {
        doTest();
    }

    public void testBinary$simple$logical_or() throws Throwable {
        doTest();
    }

    public void testBinary$nested$additive() throws Throwable {
        doTest();
    }

    public void testBinary$nested$precedence1() throws Throwable {
        doTest();
    }

    public void testBuiltin$make_channel() throws Throwable {
        doTest();
    }

    public void testBuiltin$literals() throws Throwable {
        doTest();
    }

//    @Test public void testLiterals$Composite$struct_nested() throws Throwable { doTest(); }

}
