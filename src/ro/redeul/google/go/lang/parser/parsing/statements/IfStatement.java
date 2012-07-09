package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;
import static ro.redeul.google.go.lang.parser.GoParser.ParsingFlag.AllowCompositeLiteral;

/**
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 8:01:22 PM
 */
public class IfStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if ( ! ParserUtils.lookAhead(builder, kIF) )
            return null;

        PsiBuilder.Marker marker = builder.mark();

        ParserUtils.getToken(builder, kIF);


        PsiBuilder.Marker mark = builder.mark();

        boolean allowComposite = parser.resetFlag(AllowCompositeLiteral, false);

        IElementType statementType = parser.parseStatementSimple(builder);
        if (statementType == EXPRESSION_STATEMENT && ParserUtils.lookAhead(builder,
                                                                           pLCURLY)) {
            mark.rollbackTo();
        } else {
            mark.drop();
            ParserUtils.endStatement(builder);
        }

        parser.parseExpression(builder);
        parser.resetFlag(AllowCompositeLiteral, allowComposite);

        parser.parseBody(builder);

        if (ParserUtils.lookAhead(builder, kELSE)) {
            if (builder.getTokenType() == kELSE) {
                ParserUtils.getToken(builder, kELSE);
                Statements.parse(builder, parser);
            }
        }

        marker.done(IF_STATEMENT);
        return IF_STATEMENT;
    }
}
