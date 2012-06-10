package ro.redeul.google.go.lang.parser.parsing.expressions;

import java.util.regex.Pattern;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 25, 2010
 * Time: 2:18:28 AM
 */
public class LiteralExpression implements GoElementTypes {

    static Pattern BOOLEAN_LITERAL = Pattern.compile("true|false");
    static Pattern IOTA_LITERAL = Pattern.compile("iota");

    public static boolean parse(PsiBuilder builder, GoParser parser,
                                boolean parseIota)
    {
        PsiBuilder.Marker mark = builder.mark();
        if (mIDENT == builder.getTokenType()) {
            parseIdentifier(builder, parser, parseIota);
            mark.done(LITERAL_EXPRESSION);
            return true;
        }

        if (litSTRING == builder.getTokenType()) {
            ParserUtils.eatElement(builder, LITERAL_STRING);
            mark.done(LITERAL_EXPRESSION);
            return true;
        }

        if (litCHAR == builder.getTokenType()) {
            ParserUtils.eatElement(builder, LITERAL_CHAR);
            mark.done(LITERAL_EXPRESSION);
            return true;
        }

        if (LITERALS_IMAGINARY.contains(builder.getTokenType())) {
            ParserUtils.eatElement(builder, LITERAL_IMAGINARY);
            mark.done(LITERAL_EXPRESSION);
            return true;
        }

        if (LITERALS_INT.contains(builder.getTokenType())) {
            ParserUtils.eatElement(builder, LITERAL_INTEGER);
            mark.done(LITERAL_EXPRESSION);
            return true;
        }

        if (LITERALS_FLOAT.contains(builder.getTokenType())) {
            ParserUtils.eatElement(builder, LITERAL_FLOAT);
            mark.done(LITERAL_EXPRESSION);
            return true;
        }

        mark.drop();
        return true;
    }

    private static boolean parseIdentifier(PsiBuilder builder,
                                           GoParser parser,
                                           boolean parseIota) {
        String identifier = builder.getTokenText();

        if ( BOOLEAN_LITERAL.matcher(identifier).matches() ) {
            ParserUtils.eatElement(builder, LITERAL_BOOL);
            return true;
        }

        if ( IOTA_LITERAL.matcher(identifier).matches() && parseIota ) {
            ParserUtils.eatElement(builder, LITERAL_IOTA);
            return true;
        }

        PsiBuilder.Marker mark = builder.mark();

        ParserUtils.getToken(builder, mIDENT);

        if (parser.isPackageName(identifier) &&
            ParserUtils.lookAhead(builder, oDOT)) {
            ParserUtils.getToken(builder, oDOT);
            ParserUtils.getToken(builder, mIDENT, GoBundle.message("identifier.expected"));
        }

        mark.done(GoElementTypes.LITERAL_IDENTIFIER);
        return true;
    }
}
