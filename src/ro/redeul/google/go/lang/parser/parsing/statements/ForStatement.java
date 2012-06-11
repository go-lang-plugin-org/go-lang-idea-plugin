package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.lexer.GoElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;
import static ro.redeul.google.go.lang.parser.GoParser.ParsingFlag.AllowCompositeLiteral;

public class ForStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!ParserUtils.lookAhead(builder, kFOR))
            return null;

        PsiBuilder.Marker marker = builder.mark();
        ParserUtils.getToken(builder, kFOR);

        ParserUtils.skipNLS(builder);

        boolean allowComposite;
        allowComposite = parser.resetFlag(AllowCompositeLiteral, false);

        GoElementType forType = FOR_WITH_CONDITION_STATEMENT;
        if ( builder.getTokenType() != pLCURCLY ) {
            forType = parseConditionOrForClauseOrRangeClause(builder, parser);
        }

        parser.resetFlag(AllowCompositeLiteral, allowComposite);

        parser.parseBody(builder);

        marker.done(forType);
        return forType;
    }

    private static GoElementType parseConditionOrForClauseOrRangeClause(
        PsiBuilder builder, GoParser parser)
    {
        PsiBuilder.Marker clause = builder.mark();

        IElementType statementType = parser.parseStatementSimple(builder);

        if (statementType != null && ParserUtils.lookAhead(builder, oSEMI)) {
            clause.drop();
            ParserUtils.getToken(builder, oSEMI);

            ParserUtils.skipNLS(builder);
            parser.parseExpression(builder);
            ParserUtils.getToken(builder, oSEMI, "semicolon.expected");

            ParserUtils.skipNLS(builder);
            parser.parseStatementSimple(builder);

            return FOR_WITH_CLAUSES_STATEMENT;
        }

        if (statementType == EXPRESSION_STATEMENT && ParserUtils.lookAhead(builder, pLCURCLY)) {
            clause.rollbackTo();
            parser.parseExpression(builder);
            return FOR_WITH_CONDITION_STATEMENT;
        }

        clause.rollbackTo();
        tryParseRangeClause(builder, parser);
        return FOR_WITH_RANGE_STATEMENT;
    }

    private static boolean tryParseRangeClause(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker m = builder.mark();

        parser.parseExpressionList(builder);

        if ( builder.getTokenType() == oVAR_ASSIGN || builder.getTokenType() == oASSIGN ) {

            ParserUtils.advance(builder);
            ParserUtils.skipNLS(builder);

            if ( builder.getTokenType() == kRANGE ) {
                ParserUtils.getToken(builder, kRANGE);
                parser.parseExpression(builder);

                m.drop();
                return true;
            }
        }

        m.rollbackTo();
        return false;
    }
}
