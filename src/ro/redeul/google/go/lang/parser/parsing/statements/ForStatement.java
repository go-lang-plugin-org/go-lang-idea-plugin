package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.lexer.GoElementType;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;
import static ro.redeul.google.go.lang.parser.GoParser.ParsingFlag.AllowCompositeLiteral;

class ForStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!ParserUtils.lookAhead(builder, kFOR))
            return null;

        PsiBuilder.Marker marker = builder.mark();
        ParserUtils.getToken(builder, kFOR);

        boolean allowComposite;
        allowComposite = parser.resetFlag(AllowCompositeLiteral, false);

        GoElementType forType = FOR_WITH_CONDITION_STATEMENT;
        if ( builder.getTokenType() != pLCURLY) {
            forType = parseConditionOrForClauseOrRangeClause(builder, parser);
        }

        parser.resetFlag(AllowCompositeLiteral, allowComposite);

        parser.parseBody(builder);
        marker.done(forType);
        return forType;
    }

    private static final TokenSet RANGE_LOOKAHEAD = TokenSet.create(oCOMMA, oASSIGN, oVAR_ASSIGN);

    private static GoElementType parseConditionOrForClauseOrRangeClause(
        PsiBuilder builder, GoParser parser)
    {
        PsiBuilder.Marker clause = builder.mark();

        IElementType statementType = parser.parseStatementSimple(builder);

        if (statementType == EXPRESSION_STATEMENT && ParserUtils.lookAhead(builder,
                                                                           pLCURLY)) {
            clause.rollbackTo();
            parser.parseExpression(builder);
            return FOR_WITH_CONDITION_STATEMENT;
        }

        if (statementType == EXPRESSION_STATEMENT && ParserUtils.lookAhead(builder, RANGE_LOOKAHEAD)) {
            clause.rollbackTo();
            tryParseRangeClause(builder, parser);
            return FOR_WITH_RANGE_STATEMENT;
        }

        if ( statementType == null ) {
            if (tryParseRangeClause(builder, parser)) {
                clause.drop();
                return FOR_WITH_RANGE_STATEMENT;
            }
        }

        clause.drop();
        if (ParserUtils.getToken(builder, GoTokenTypeSets.EOS) ) {
            parser.parseExpression(builder);
            if (ParserUtils.getToken(builder, GoTokenTypeSets.EOS)) {
                parser.parseStatementSimple(builder);
            } else {
                builder.error(GoBundle.message("error.semicolon.or.newline.expected"));
            }
        } else {
            builder.error(GoBundle.message("error.semicolon.or.newline.expected"));
        }
        return FOR_WITH_CLAUSES_STATEMENT;
    }

    private static boolean tryParseRangeClause(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker m = builder.mark();

        parser.parseExpressionList(builder);

        if ( builder.getTokenType() == oVAR_ASSIGN || builder.getTokenType() == oASSIGN ) {
            ParserUtils.advance(builder);

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
