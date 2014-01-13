package ro.redeul.google.go.lang.parser.parsing.expressions;

import com.google.common.collect.ImmutableSet;
import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

import java.util.Set;

/**
 * User: mtoader
 * Date: Aug 16, 2010
 * Time: 7:53:26 AM
 */
class BuiltInCallExpression implements GoElementTypes {

    private static final Set<String> hasTypeParameter = ImmutableSet.of(
        "new",
        "make"
    );

    private static final  Set<String> noTypeParameter = ImmutableSet.of(
        "append",
        "cap",
        "close",
        "complex",
        "copy",
        "delete",
        "imag",
        "len",
        "panic",
        "print",
        "println",
        "real",
        "recover"
    );

    private static final  Set<String> defaultConversions = ImmutableSet.of(
        "uint8",
        "uint16",
        "uint32",
        "uint64",
        "int8",
        "int16",
        "int32",
        "int64",
        "float32",
        "float64",
        "complex64",
        "complex128",
        "byte",
        "rune",
        "uint",
        "int",
        "uintptr",
        "string",
        "error",
        "bool"
    );


    private static boolean isBuiltInCall(String methodCall) {
        return
            defaultConversions.contains(methodCall) ||
                hasTypeParameter.contains(methodCall) ||
                noTypeParameter.contains(methodCall);
    }

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        String callName = builder.getTokenText();

        if (!ParserUtils.lookAhead(builder, mIDENT, pLPAREN))
            return false;

        if (!isBuiltInCall(callName))
            return false;

        PsiBuilder.Marker mark = builder.mark();
        ParserUtils.eatElement(builder, LITERAL_IDENTIFIER);
        mark.done(LITERAL_EXPRESSION);
        mark = mark.precede();
        ParserUtils.getToken(builder, pLPAREN, "open.parenthesis.expected");

        if (hasTypeParameter.contains(callName)) {
            parser.parseType(builder);
            if (oCOMMA == builder.getTokenType()) {
                builder.advanceLexer();
            }
        }

        if (builder.getTokenType() != pRPAREN) {
            parser.parseExpressionList(builder);
        }

        ParserUtils.getToken(builder, pRPAREN, "closed.parenthesis.expected");

        mark.done(BUILTIN_CALL_EXPRESSION);

        return true;
    }
}
