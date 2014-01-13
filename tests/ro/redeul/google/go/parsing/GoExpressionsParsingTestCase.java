package ro.redeul.google.go.parsing;

import ro.redeul.google.go.GoParsingTestCase;

/**
 * TODO: Document this
 * <p/>
 * Created on Jan-12-2014 16:15
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoExpressionsParsingTestCase extends GoParsingTestCase {

    @Override
    protected String getRelativeTestDataPath() {
        return super.getRelativeTestDataPath() + "expressions";
    }

    public void testBinary_nested_additive() throws Throwable { _test(); }
    public void testBinary_nested_precedence1() throws Throwable { _test(); }

    public void testBinary_simple_additive() throws Throwable { _test(); }
    public void testBinary_simple_communication() throws Throwable { _test(); }
    public void testBinary_simple_logicalAnd() throws Throwable { _test(); }
    public void testBinary_simple_logicalOr() throws Throwable { _test(); }
    public void testBinary_simple_multiplicative() throws Throwable { _test(); }
    public void testBinary_simple_relational() throws Throwable { _test(); }
    public void testBinary_simple_substraction() throws Throwable { _test(); }

    public void testCallOrConversion_arrayConversion() throws Throwable { _test(); }
    public void testCallOrConversion_interfaceConversion() throws Throwable { _test(); }
    public void testCallOrConversion_parameter() throws Throwable { _test(); }
    public void testCallOrConversion_parameters() throws Throwable { _test(); }
    public void testCallOrConversion_parenthesised() throws Throwable { _test(); }
    public void testCallOrConversion_simple() throws Throwable { _test(); }

    public void testBuiltin_makeChannel() throws Throwable { _test(); }
    public void testBuiltin_literals() throws Throwable { _test(); }

    public void testCompositeLiteral_array() throws Throwable { _test(); }
    public void testCompositeLiteral_arrayImplicitLength() throws Throwable { _test(); }
    public void testCompositeLiteral_builtins() throws Throwable { _test(); }
    public void testCompositeLiteral_error1() throws Throwable { _test(); }
    public void testCompositeLiteral_map() throws Throwable { _test(); }
    public void testCompositeLiteral_noCommaBeforeNewline() throws Throwable { _test(); }
    public void testCompositeLiteral_slice() throws Throwable { _test(); }
    public void testCompositeLiteral_struct() throws Throwable { _test(); }
    public void testCompositeLiteral_structNested() throws Throwable { _test(); }
    public void testCompositeLiteral_structNestedValue() throws Throwable { _test(); }
    public void testCompositeLiteral_type1() throws Throwable { _test(); }
    public void testCompositeLiteral_type2() throws Throwable { _test(); }

    public void testIndex_expression() throws Throwable { _test(); }
    public void testIndex_simple() throws Throwable { _test(); }
    public void testIndex_structLiteral() throws Throwable { _test(); }

    public void testLiteral_char() throws Throwable { _test(); }
    public void testLiteral_float() throws Throwable { _test(); }
    public void testLiteral_function1() throws Throwable { _test(); }
    public void testLiteral_function2() throws Throwable { _test(); }
    public void testLiteral_identifier() throws Throwable { _test(); }
    public void testLiteral_imaginary() throws Throwable { _test(); }
    public void testLiteral_integer() throws Throwable { _test(); }
    public void testLiteral_qualifiedIdentifier() throws Throwable { _test(); }
    public void testLiteral_string1() throws Throwable { _test(); }
    public void testLiteral_string2() throws Throwable { _test(); }

    public void testParenthesised_basic() throws Throwable { _test(); }
    public void testParenthesised_indirection() throws Throwable { _test(); }

    public void testPrimary_call_simple() throws Throwable { _test(); }
    public void testPrimary_call_multiple() throws Throwable { _test(); }
    public void testPrimary_selector_exported() throws Throwable { _test(); }
    public void testPrimary_selector_variable() throws Throwable { _test(); }
    public void testPrimary_slice_simple() throws Throwable { _test(); }
    public void testPrimary_composite() throws Throwable { _test(); }
    public void testPrimary_index() throws Throwable { _test(); }
    public void testPrimary_parenthesised() throws Throwable { _test(); }
    public void testPrimary_variableQualified() throws Throwable { _test(); }

    public void testReferences_reference1() throws Throwable { _test(); }

    public void testSelector_double() throws Throwable { _test(); }
    public void testSelector_incomplete() throws Throwable { _test(); }
    public void testSelector_multiple() throws Throwable { _test(); }
    public void testSelector_simple() throws Throwable { _test(); }

    public void testSlice_expressions() throws Throwable { _test(); }
    public void testSlice_simple() throws Throwable { _test(); }
    public void testSlice_withCapacity() throws Throwable { _test(); }

    public void testTypeAssertion_simple() throws Throwable { _test(); }
    public void testTypeAssertion_type() throws Throwable { _test(); }
}
