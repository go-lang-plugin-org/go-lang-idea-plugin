package ro.redeul.google.go.lang.parser.parsing.expressions;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

public class Expressions implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser,
                                boolean inControlExpressions, boolean parseIota) {
        return BinaryExpression.parse(builder, parser,
                                      inControlExpressions, parseIota);
    }

    public static int parseList(PsiBuilder builder, GoParser parser,
                                boolean inControlStmts, boolean parseIota) {

//        PsiBuilder.Marker marker = builder.mark();

        int count = 0;
        do {

            if ( parse(builder, parser, inControlStmts, parseIota) ) {
                count++;
            }

            if ( builder.getTokenType() == wsNLS || builder.getTokenType() == oSEMI ) {
                break;
            }

            ParserUtils.getToken(builder, oTRIPLE_DOT);

            if  (builder.getTokenType() == oCOMMA ) {
                ParserUtils.getToken(builder, oCOMMA);
                ParserUtils.skipNLS(builder);
            } else {
                break;
            }

        } while ( ! builder.eof() );

//        if ( count > 1 ) {
//            marker.done(EXPRESSION_LIST);
//        } else {
//            marker.drop();
//        }

        return count;
    }

    /**
     * PrimaryExpr :=
     *                  int_lit | float_lit | imaginary_lit | char_lit | string_lit |
     *                  "func"                                              -> function literal
     *                  "(" Expression ")"                                  -> ExpressionLiteral
     *                  "(" Type ") "{"                                     -> CompositeLiteral
     *                  "(" "*" [ PackageName "." ] identifier ")" "."      -> MethodExpr
     *                  identifier "("                                      -> BuiltinCall

     *                  [ PackageName "." ] identifier                      -> QualifiedIdent     *
     *                  [ PackageName "." ] identifier "."                  -> MethodExpr
     *                  Type "{"                                            -> CompositeLiteral
     *                  Type "(" Expression ")"                             -> ConversionCall
     *
     *
     *                  PrimaryExpr Selector |
     *                  PrimaryExpr Index |
     *                  PrimaryExpr Slice |
     *                  PrimaryExpr TypeAssertion |
     *                  PrimaryExpr Call .
     *
     * @param builder the psi builder
     * @param parser the actual go parser to use
     * @param inControlStmts if we are parsing expressions inside an if/for type of control expressions
     *
     * @return true/false depending on how successful we were
     */
    public static boolean parsePrimary(PsiBuilder builder, GoParser parser,
                                       boolean inControlStmts, boolean parseIota) {
        return PrimaryExpression.parse(builder, parser, inControlStmts, parseIota);
    }

//    private static boolean parseBuiltInCall(PsiBuilder builder) {
//        PsiBuilder.Marker mark = builder.mark();
//
//        ParserUtils.eatElement(builder, IDENTIFIER);
//
//        ParserUtils.getToken(builder, IDENTIFIER);
//
//        BUILTIN_FUNCTION
//    }
//
//    private static Set<String> builtInCalls = new HashSet<String>(Arrays.asList(
//        "cap", "close", "closed", "cmplx", "copy", "imag", "len", "make", "new", "panic", "print", "println", "real"
//    ));
//
//    private static boolean isBuiltInCall(String text) {
//        return builtInCalls.contains(text);
//    }
}
