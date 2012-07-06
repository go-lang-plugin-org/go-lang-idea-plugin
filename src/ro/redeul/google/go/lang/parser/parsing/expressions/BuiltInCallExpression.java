package ro.redeul.google.go.lang.parser.parsing.expressions;

import java.util.HashSet;
import java.util.Set;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * User: mtoader
 * Date: Aug 16, 2010
 * Time: 7:53:26 AM
 */
public class BuiltInCallExpression implements GoElementTypes {

    static Set<String> hasTypeParameter = new HashSet<String>() {{
        add("new");
        add("make");
    }};

    static Set<String> noTypeParameter = new HashSet<String>() {{
        add("append");
        add("cap");
        add("close");
        add("complex");
        add("copy");
        add("delete");
        add("imag");
        add("len");
        add("panic");
        add("print");
        add("println");
        add("real");
        add("recover");
    }};

    static Set<String> defaultConversions = new HashSet<String>() {{
        add("uint8");
        add("uint16");
        add("uint32");
        add("uint64");
        add("int8");
        add("int16");
        add("int32");
        add("int64");
        add("float32");
        add("float64");
        add("complex64");
        add("complex128");
        add("byte");
        add("rune");
        add("uint");
        add("int");
        add("uintptr");
        add("string");
        add("error");
        add("bool");
    }};


    public static boolean isBuiltInCall(String methodCall) {
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
                ParserUtils.skipNLS(builder);
            }
        }

        if (builder.getTokenType() != pRPAREN) {
            PsiBuilder.Marker expressionList = builder.mark();
            if (parser.parseExpressionList(builder) > 1) {
                expressionList.done(GoElementTypes.EXPRESSION_LIST);
            } else {
                expressionList.drop();
            }
        }

        ParserUtils.getToken(builder, pRPAREN, "closed.parenthesis.expected");

        mark.done(BUILTIN_CALL_EXPRESSION);

        return true;
    }
}
