package ro.redeul.google.go.lang.parser.parsing.expressions;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;
import ro.redeul.google.go.lang.psi.utils.GoTokenSets;

/**
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 2:18:28 AM
 */
public class UnaryExpression implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker mark = builder.mark();

        if ( ParserUtils.getToken(builder, GoTokenSets.UNARY_OPS)) {
            parse(builder, parser);
            mark.done(UNARY_EXPRESSION);
            return true;
        }

        if ( oSEND_CHANNEL == builder.getTokenType() ) {

            PsiBuilder.Marker marker = builder.mark();
            ParserUtils.getToken(builder, builder.getTokenType());

            ParserUtils.skipNLS(builder);
            if ( kCHAN == builder.getTokenType() ) {
                ParserUtils.advance(builder);

                ParserUtils.skipNLS(builder);
                parser.parseType(builder);

                marker.error("chan.type.should.not.be.here");
                mark.done(UNARY_EXPRESSION);
                return true;
            } else {
                marker.drop();
            }

            parse(builder, parser);
            mark.done(UNARY_EXPRESSION);
            return true;
        }

        mark.rollbackTo();

        return parser.parsePrimaryExpression(builder);
    }
}
