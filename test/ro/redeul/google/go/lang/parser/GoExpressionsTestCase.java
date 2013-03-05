package ro.redeul.google.go.lang.parser;

import java.io.File;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Aug 5, 2010
 */
public class GoExpressionsTestCase extends GoParsingTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + File.separator + "expressions";
    }

    public void testCallOrConversion_parameter() throws Throwable {
        doTest();
    }

    public void testCallOrConversion_simple() throws Throwable {
        doTest();
    }

    public void testCallOrConversion_parameters() throws Throwable {
        doTest();
    }

    public void testCallOrConversion_parenthesised() throws Throwable {
        doTest();
    }

    public void testCallOrConversion_arrayConversion() throws Throwable {
        doTest();
    }

    public void testIndex_simple() throws Throwable {
        doTest();
    }

    public void testIndex_expression() throws Throwable {
        doTest();
    }

    public void testSlice_simple() throws Throwable {
        doTest();
    }

    public void testSlice_expressions() throws Throwable {
        doTest();
    }

    public void testTypeAssertion_simple() throws Throwable {
        doTest();
    }

    public void testTypeAssertion_type() throws Throwable {
        doTest();
    }

    public void testSelector_simple() throws Throwable {
        doTest();
    }

    public void testSelector_double() throws Throwable {
        doTest();
    }

    public void testSelector_incomplete() throws Throwable {
        doTest();
    }

    public void testParenthesised_basic() throws Throwable {
        doTest();
    }

    public void testParenthesised_indirection() throws Throwable {
        doTest();
    }

    public void testLiteral_QualifiedIdentifier() throws Throwable {
        doTest();
    }

    public void testLiteral_identifier() throws Throwable {
        doTest();
    }

    public void testLiteral_integer() throws Throwable {
        doTest();
    }

    public void testLiteral_float() throws Throwable {
        doTest();
    }

    public void testLiteral_imaginary() throws Throwable {
        doTest();
    }

    public void testLiteral_char() throws Throwable {
        doTest();
    }

    public void testLiteral_string() throws Throwable {
        doTest();
    }

    public void testLiteral_string2() throws Throwable {
        doTest();
    }

    public void testCompositeLiteral_error1() throws Throwable {
        doTest();
    }

    public void testCompositeLiteral_noCommaBeforeNewline() throws Throwable {
        doTest();
    }

    public void testCompositeLiteral_builtins() throws Throwable {
        doTest();
    }

    public void testCompositeLiteral_struct() throws Throwable {
        doTest();
    }

    public void testCompositeLiteral_structNested() throws Throwable {
        doTest();
    }

    public void testCompositeLiteral_structNestedValue() throws Throwable {
        doTest();
    }

    public void testCompositeLiteral_slice() throws Throwable {
        doTest();
    }

    public void testCompositeLiteral_array() throws Throwable {
        doTest();
    }

    public void testCompositeLiteral_arrayImplicitLength() throws Throwable {
        doTest();
    }

    public void testCompositeLiteral_map() throws Throwable {
        doTest();
    }

    public void testCompositeLiteral_type() throws Throwable {
        doTest();
    }

    public void testCompositeLiteral_type2() throws Throwable {
        doTest();
    }

    public void testLiteral_function1() throws Throwable {
        doTest();
    }

    public void testLiteral_function2() throws Throwable {
        doTest();
    }

    public void testPrimary_primary1() throws Throwable {
        doTest();
    }

    public void testPrimary_primary2() throws Throwable {
        doTest();
    }

    public void testPrimary_primary3() throws Throwable {
        doTest();
    }

    public void testPrimary_primary4() throws Throwable {
        doTest();
    }

    public void testPrimary_primary5() throws Throwable {
        doTest();
    }

    public void testPrimary_primary6() throws Throwable {
        doTest();
    }

    public void testPrimary_primary7() throws Throwable {
        doTest();
    }

    public void testPrimary_primary7Qualified() throws Throwable {
        doTest();
    }

    public void testPrimary_primary8() throws Throwable {
        doTest();
    }

    public void testPrimary_primary9() throws Throwable {
        doTest();
    }

    public void testBinary_simple_additive() throws Throwable {
        doTest();
    }

    public void testBinary_simple_substraction() throws Throwable {
        doTest();
    }

    public void testBinary_simple_multiplicative() throws Throwable {
        doTest();
    }

    public void testBinary_simple_relational() throws Throwable {
        doTest();
    }

    public void testBinary_simple_communication() throws Throwable {
        doTest();
    }

    public void testBinary_simple_logicalAnd() throws Throwable {
        doTest();
    }

    public void testBinary_simple_logicalOr() throws Throwable {
        doTest();
    }

    public void testBinary_nested_additive() throws Throwable {
        doTest();
    }

    public void testBinary_nested_precedence1() throws Throwable {
        doTest();
    }

    public void testBuiltin_makeChannel() throws Throwable {
        doTest();
    }

    public void testBuiltin_literals() throws Throwable {
        doTest();
    }

    public void testReferences_reference1() throws Throwable {
        doTest();
    }
}
