package ro.redeul.google.go.lang.parser.parsing.expressions;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 2:18:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class UnaryExpression implements GoElementTypes {

    static TokenSet UNARY_OPS = TokenSet.create(
            oPLUS, oMINUS, oNOT, oBIT_XOR, oBIT_AND, kRANGE, oMUL
    );
    
    public static boolean parse(PsiBuilder builder, GoParser parser, boolean inControlStmts) {

        ParserUtils.skipNLS(builder);

        PsiBuilder.Marker mark = builder.mark();

        if ( UNARY_OPS.contains(builder.getTokenType()) ) {
            ParserUtils.getToken(builder, builder.getTokenType());

            parse(builder, parser, inControlStmts);
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

            parse(builder, parser, inControlStmts);
            mark.done(UNARY_EXPRESSION);
            return true;
        }

        mark.rollbackTo();
        
        return parser.parsePrimaryExpression(builder, inControlStmts);
    }
}
