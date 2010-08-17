package ro.redeul.google.go.lang.parser.parsing.helpers;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 6:17:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdentifierList implements GoElementTypes {

    public static int parse(PsiBuilder builder, GoParser parser) {

        int length = 0;

        ParserUtils.skipNLS(builder);
        PsiBuilder.Marker list = builder.mark();

        if ( ! (builder.getTokenType() == mIDENT) ) {
            list.rollbackTo();
            return length;
        }

        while ( ParserUtils.getToken(builder, mIDENT) ) {

            length++;
            if ( ! (builder.getTokenType() == oCOMMA) ) {
                break;
            }

            builder.advanceLexer();
        }

        list.done(IDENTIFIERS);
        return length;
    }

}
