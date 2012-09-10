package ro.redeul.google.go.lang.parser.parsing.types;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 2:50:17 AM
 */
public class SliceType implements GoElementTypes  {
    public static IElementType parse(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, pLBRACK)) {
            marker.drop();
            builder.error("left.bracket.expected");
            return null;
        }

        if (!ParserUtils.getToken(builder, pRBRACK)) {
            builder.error("right.bracket.expected");
        }

        if ( parser.parseType(builder) == null ) {
            marker.drop();
            builder.error("type declaration expected");
            return null;
        }

        marker.done(TYPE_SLICE);
        return TYPE_SLICE;
    }
}
