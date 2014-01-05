package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

import static ro.redeul.google.go.lang.parser.GoParser.ParsingFlag.AllowCompositeLiteral;
import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.*;

class ForStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!lookAhead(builder, kFOR))
            return null;

        PsiBuilder.Marker marker = builder.mark();
        getToken(builder, kFOR);

        boolean allowComposite;
        allowComposite = parser.resetFlag(AllowCompositeLiteral, false);

        IElementType forType = FOR_WITH_CONDITION_STATEMENT;
        if ( builder.getTokenType() != pLCURLY) {
            forType = parseConditionOrForClauseOrRangeClause(builder, parser);
            if (forType == null)
                forType = FOR_WITH_CONDITION_STATEMENT;
        }

        parser.resetFlag(AllowCompositeLiteral, allowComposite);

        parser.parseBody(builder);
        
        return completeStatement(builder, marker, forType);
    }

    private static final TokenSet RANGE_LOOKAHEAD = TokenSet.create(oCOMMA, oASSIGN, oVAR_ASSIGN);

    private static IElementType parseConditionOrForClauseOrRangeClause(
        PsiBuilder builder, GoParser parser)
    {
        PsiBuilder.Marker clause = builder.mark();

        IElementType statementType = Statements.parseSimple(builder, parser, false);
        IElementType forStatementType = null;

        if (statementType == EXPRESSION_STATEMENT && lookAhead(builder,
            pLCURLY)) {
            clause.rollbackTo();
            parser.parseExpression(builder);
            return FOR_WITH_CONDITION_STATEMENT;
        }

        if (statementType == EXPRESSION_STATEMENT && lookAhead(builder, RANGE_LOOKAHEAD)) {
            clause.rollbackTo();
            return tryParseRangeClause(builder, parser);
        }

        if ( statementType == null ) {
            if ((forStatementType = tryParseRangeClause(builder, parser)) != null) {
                clause.drop();
                return forStatementType;
            }
        }

        clause.drop();
        if (getToken(builder, GoTokenTypeSets.EOS) ) {
            parser.parseExpression(builder);
            if (getToken(builder, GoTokenTypeSets.EOS)) {
                Statements.parseSimple(builder, parser, false);
            } else {
                builder.error(GoBundle.message("error.semicolon.or.newline.expected"));
            }
        } else {
            builder.error(GoBundle.message("error.semicolon.or.newline.expected"));
        }
        return FOR_WITH_CLAUSES_STATEMENT;
    }

    private static IElementType tryParseRangeClause(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker m = builder.mark();

        parser.parseIdentifierList(builder, false);

        if ( lookAhead(builder, oVAR_ASSIGN, kRANGE) ) {
            getToken(builder, oVAR_ASSIGN);
            getToken(builder, kRANGE);
            parser.parseExpression(builder);
            m.drop();
            return FOR_WITH_RANGE_AND_VARS_STATEMENT;
        }

        if ( lookAhead(builder, oASSIGN, kRANGE) ) {
            m.rollbackTo();
            parser.parseExpressionList(builder);
            getToken(builder, oASSIGN);
            getToken(builder, kRANGE);
            parser.parseExpression(builder);
            return FOR_WITH_RANGE_STATEMENT;
        }

        m.rollbackTo();
        if ( parser.parseExpressionList(builder) != 0 && lookAhead(builder, oASSIGN, kRANGE) ) {
            getToken(builder, oASSIGN);
            getToken(builder, kRANGE);
            parser.parseExpression(builder);
            return FOR_WITH_RANGE_STATEMENT;
        }

        return null;
    }
}
