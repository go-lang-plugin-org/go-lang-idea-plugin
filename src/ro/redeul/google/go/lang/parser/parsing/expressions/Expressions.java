package ro.redeul.google.go.lang.parser.parsing.expressions;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 12:28:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class Expressions implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser, boolean inControlExpressions) {
        return BinaryExpression.parse(builder, parser, inControlExpressions);
    }

    public static int parseList(PsiBuilder builder, GoParser parser, boolean inControlStmts) {

        PsiBuilder.Marker marker = builder.mark();

        int count = 0;
        do {

            if ( parse(builder, parser, inControlStmts) ) {
                count++;
            }

            if ( builder.getTokenType() == wsNLS || builder.getTokenType() == oSEMI ) {
                break;
            }
            
            if  (builder.getTokenType() == oCOMMA ) {
                ParserUtils.getToken(builder, oCOMMA);
                ParserUtils.skipNLS(builder);
            } else {
                break;
            }

        } while ( ! builder.eof() );

        if ( count > 1 ) {
            marker.done(EXPRESSION_LIST);
        } else {
            marker.drop();
        }

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
     */
    public static boolean parsePrimary(PsiBuilder builder, GoParser parser, boolean inControlStmts) {
        return PrimaryExpression.parse(builder, parser, inControlStmts);
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
