package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

public class ReturnStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {


        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kRETURN)) {
            marker.rollbackTo();
            return null;
        }

        if (!ParserUtils.lookAhead(builder, GoTokenTypeSets.EOS)) {
            parser.tryParseExpressionList(builder);
        }

        marker.done(RETURN_STATEMENT);
        return RETURN_STATEMENT;
    }
}
