package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
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
public class DeferStatement implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kDEFER)) {
            marker.rollbackTo();
            return false;
        }

        parser.parseExpression(builder, false);
        marker.done(DEFER_STATEMENT);
        return true;

    }
}
