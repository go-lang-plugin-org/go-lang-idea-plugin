package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

import static ro.redeul.google.go.lang.parser.GoParser.ParsingFlag.AllowCompositeLiteral;
import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.*;

/**
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 8:01:22 PM
 */
class IfStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {
        return parse(builder, parser, true);
    }

    public static IElementType parse(PsiBuilder builder, GoParser parser, boolean complete) {

        if ( ! lookAhead(builder, kIF) )
            return null;

        PsiBuilder.Marker marker = builder.mark();

        getToken(builder, kIF);

        PsiBuilder.Marker mark = builder.mark();

        boolean allowComposite = parser.resetFlag(AllowCompositeLiteral, false);

        IElementType statementType = Statements.parseSimple(builder, parser, false);
        if (statementType == EXPRESSION_STATEMENT &&
            lookAhead(builder, pLCURLY)) {
            mark.rollbackTo();
        } else {
            mark.drop();
            endStatement(builder);
        }

        parser.parseExpression(builder);
        parser.resetFlag(AllowCompositeLiteral, allowComposite);

        parser.parseBody(builder);

        if (lookAhead(builder, kELSE)) {
            if (builder.getTokenType() == kELSE) {
                getToken(builder, kELSE);
                if (lookAhead(builder, kIF))
                    IfStatement.parse(builder, parser, false);
                else if (lookAhead(builder, pLCURLY))
                    parser.parseBody(builder);
                else {
                    builder.error(GoBundle.message("error.block.of.if.statement.expected"));
                }
            }
        }

        if (complete)
            return completeStatement(builder, marker, IF_STATEMENT);
        else {
            marker.done(IF_STATEMENT);
            return IF_STATEMENT;
        }
    }
}
