package ro.redeul.google.go.lang.parser.parsing.helpers;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 25, 2010
 * Time: 6:17:19 PM
 */
public class IdentifierList implements GoElementTypes {

    public static int parse(PsiBuilder builder) {
        return parse(builder, true);
    }

    public static int parse(PsiBuilder builder, boolean markList) {

        int length = 0;

        PsiBuilder.Marker list = null;
        if ( markList ) {
            list = builder.mark();
        }

        if ( ! (builder.getTokenType() == mIDENT) ) {
            if (markList) {
                list.rollbackTo();
            }
            return length;
        }

        while ( ParserUtils.lookAhead(builder, mIDENT) ) {
            ParserUtils.eatElement(builder, GoElementTypes.LITERAL_IDENTIFIER);

            length++;
            if (!ParserUtils.lookAhead(builder, oCOMMA, mIDENT)) {
                break;
            }

            ParserUtils.getToken(builder, oCOMMA);
        }

        if (markList ) {
            list.done(IDENTIFIERS);
        }

        return length;
    }

}
