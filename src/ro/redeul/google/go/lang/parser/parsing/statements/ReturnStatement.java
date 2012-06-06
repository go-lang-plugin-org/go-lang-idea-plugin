package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

public class ReturnStatement implements GoElementTypes {
    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kRETURN)) {
            marker.rollbackTo();
            return false;
        }

        PsiBuilder.Marker mark = builder.mark();
        if ( parser.parseExpressionList(builder, false) == 0 ) {
            mark.rollbackTo();
        } else {
            mark.drop();
        }

        ParserUtils.getToken(builder, oSEMI);
        marker.done(RETURN_STATEMENT);
        return true;

    }
}
