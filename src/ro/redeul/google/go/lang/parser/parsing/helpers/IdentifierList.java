package ro.redeul.google.go.lang.parser.parsing.helpers;

import com.intellij.lang.PsiBuilder;
import org.apache.velocity.runtime.directive.Parse;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 25, 2010
 * Time: 6:17:19 PM
 */
public class IdentifierList implements GoElementTypes {

    public static int parse(PsiBuilder builder, GoParser parser) {
        return parse(builder, parser, true);
    }

    public static int parse(PsiBuilder builder, GoParser parser, boolean markList) {

        int length = 0;

        ParserUtils.skipNLS(builder);
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
            ParserUtils.eatElement(builder, GoElementTypes.IDENTIFIER);

            length++;
            if ( ! (builder.getTokenType() == oCOMMA) ) {
                break;
            }

            builder.advanceLexer();
        }

        if (markList ) {
            list.done(IDENTIFIERS);
        }

        return length;
    }

}
