package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 16, 2010
 * Time: 8:06:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class LabeledStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.lookAhead(builder, mIDENT, oCOLON)) {
            marker.rollbackTo();
            return null;
        }

        ParserUtils.getToken(builder, mIDENT);
        ParserUtils.getToken(builder, oCOLON);

        ParserUtils.skipNLS(builder);
        parser.parseStatement(builder);
        marker.done(LABELED_STATEMENT);
        return LABELED_STATEMENT;
    }
}
