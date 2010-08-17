package ro.redeul.google.go.lang.parser.parsing.types;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 2:50:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class SliceType implements GoElementTypes  {
    public static boolean parse(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, pLBRACK)) {
            marker.drop();
            builder.error("left.bracket.expected");
            return false;
        }

        if (!ParserUtils.getToken(builder, pRBRACK)) {
            builder.error("right.bracket.expected");
        }

        parser.parseType(builder);

        marker.done(TYPE_SLICE);
        return true;

    }
}
