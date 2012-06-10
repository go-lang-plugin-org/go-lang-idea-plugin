package ro.redeul.google.go.lang.parser.parsing.expressions;

import java.util.HashSet;
import java.util.Set;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 16, 2010
 * Time: 7:53:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class BuiltInCallExpression implements GoElementTypes {

    static Set<String> hasTypeParameter = new HashSet<String>() {{
        add("new");
        add("make");
    }};

    // cap close closed cmplx copy imag len make
	// new panic print println real recover

    static Set<String> noTypeParameter = new HashSet<String>() {{
        add("cap");
        add("close");
        add("closed");
        add("cmplx");
        add("copy");
        add("imag");
        add("len");
        add("panic");
        add("print");
        add("println");
        add("real");
        add("recover");
    }};


    public static boolean isBuiltInCall(String methodCall) {
        return hasTypeParameter.contains(methodCall) || noTypeParameter.contains(methodCall);
    }

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker mark = builder.mark();

        String callName = builder.getTokenText();

        if ( builder.getTokenType() != mIDENT || (!hasTypeParameter.contains(callName) && !noTypeParameter.contains(builder.getTokenText()) )) {
            mark.drop();
            return false;
        }

        ParserUtils.eatElement(builder, LITERAL_EXPRESSION);
        ParserUtils.getToken(builder, pLPAREN, "open.parenthesis.expected");

        if ( hasTypeParameter.contains(callName) ) {
            parser.parseType(builder);
            if ( oCOMMA == builder.getTokenType() ) {
                builder.advanceLexer();
                ParserUtils.skipNLS(builder);
            }
        }

        if ( builder.getTokenType() != pRPAREN ) {
            parser.parseExpressionList(builder, false, false);
        }

        ParserUtils.getToken(builder, pRPAREN, "closed.parenthesis.expected");

        mark.done(BUILTIN_CALL_EXPRESSION);

        return true;
    }
}
