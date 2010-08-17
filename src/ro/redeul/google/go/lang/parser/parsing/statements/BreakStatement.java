package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 8:07:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class BreakStatement implements GoElementTypes {
    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kBREAK)) {
            marker.rollbackTo();
            return false;
        }

        ParserUtils.getToken(builder, mIDENT);

        marker.done(BREAK_STATEMENT);
        return true;
    }
}
