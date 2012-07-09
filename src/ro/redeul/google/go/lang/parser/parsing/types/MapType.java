package ro.redeul.google.go.lang.parser.parsing.types;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
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

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!ParserUtils.lookAhead(builder, kMAP))
            return null;

        PsiBuilder.Marker marker = builder.mark();

        ParserUtils.getToken(builder, kMAP);

        ParserUtils.getToken(builder, pLBRACK, "left.bracket.expected");

        parser.parseType(builder);

        ParserUtils.getToken(builder, pRBRACK, "right.bracket.expected");

        parser.parseType(builder);

        marker.done(TYPE_MAP);
        return TYPE_MAP;
    }
}
