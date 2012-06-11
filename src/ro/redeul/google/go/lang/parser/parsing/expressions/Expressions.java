package ro.redeul.google.go.lang.parser.parsing.expressions;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

public class Expressions implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {
        return BinaryExpression.parse(builder, parser);
    }

    public static int parseList(PsiBuilder builder, GoParser parser) {

//        PsiBuilder.Marker marker = builder.mark();

        parser.setFlag(GoParser.ParsingFlag.WrapCompositeInExpression);
        int count = 0;
        do {

            if ( parse(builder, parser) ) {
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

        parser.unsetFlag(GoParser.ParsingFlag.WrapCompositeInExpression);
        return count;
    }

    public static boolean parsePrimary(PsiBuilder builder, GoParser parser) {
        return PrimaryExpression.parse(builder, parser);
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
