package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

public class GoStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kGO)) {
            marker.rollbackTo();
            return null;
        }

        parser.parseExpression(builder);
        marker.done(GO_STATEMENT);
        return GO_STATEMENT;
    }
}
