package ro.redeul.google.go.lang.parser.parsing.types;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 2:52:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class MapType implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if ( ! ParserUtils.getToken(builder, kMAP) ) {
            marker.drop();
            builder.error("map.keyword.expected");
            return false;
        }

        ParserUtils.skipNLS(builder);
        ParserUtils.getToken(builder, pLBRACK, "left.bracket.expected");

        ParserUtils.skipNLS(builder);
        parser.parseType(builder);

        ParserUtils.skipNLS(builder);
        ParserUtils.getToken(builder, pRBRACK, "right.bracket.expected");

        parser.parseType(builder);

        marker.done(TYPE_MAP);
        return true;
    }
}
