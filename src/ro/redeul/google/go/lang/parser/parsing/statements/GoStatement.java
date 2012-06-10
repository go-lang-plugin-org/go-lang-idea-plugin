package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

public class GoStatement implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kGO)) {
            marker.rollbackTo();
            return false;
        }

        parser.parseExpression(builder, false, false);
        marker.done(GO_STATEMENT);
        return true;

    }
}
