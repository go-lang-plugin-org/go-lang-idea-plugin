package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 8:07:21 PM
 */
class BreakStatement implements GoElementTypes {
    public static IElementType parse(PsiBuilder builder) {

        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kBREAK)) {
            marker.rollbackTo();
            return null;
        }

        if (ParserUtils.lookAhead(builder, mIDENT))
            ParserUtils.eatElement(builder, LITERAL_IDENTIFIER);

        marker.done(BREAK_STATEMENT);
        return BREAK_STATEMENT;
    }
}
