package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.lexer.GoElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

public class ForStatement implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kFOR)) {
            marker.rollbackTo();
            return false;
        }

        ParserUtils.skipNLS(builder);

        GoElementType forType = FOR_WITH_CONDITION_STATEMENT;
        if ( builder.getTokenType() != pLCURCLY ) {
            forType = parseConditionOrForClauseOrRangeClause(builder, parser);
        }

        parser.parseBody(builder);

        marker.done(forType);
        return true;

    }

    private static GoElementType parseConditionOrForClauseOrRangeClause(
        PsiBuilder builder, GoParser parser)
    {
        PsiBuilder.Marker clause = builder.mark();
        parser.parseExpression(builder, true, false);
        if ( pLCURCLY == builder.getTokenType() ) {
            clause.drop();
            return FOR_WITH_CONDITION_STATEMENT;
        }

        clause.rollbackTo();

        if ( tryParseRangeClause(builder, parser) ) {
            return FOR_WITH_RANGE_STATEMENT;
        }

        parser.parseStatementSimple(builder, true);
        ParserUtils.getToken(builder, oSEMI, "semicolon.expected");

        ParserUtils.skipNLS(builder);
        parser.parseExpression(builder, true, false);
        ParserUtils.getToken(builder, oSEMI, "semicolon.expected");

        ParserUtils.skipNLS(builder);
        parser.parseStatementSimple(builder, true);

        return FOR_WITH_CLAUSES_STATEMENT;
    }

    private static boolean tryParseRangeClause(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker m = builder.mark();

        parser.parseExpressionList(builder, true, false);

        if ( builder.getTokenType() == oVAR_ASSIGN || builder.getTokenType() == oASSIGN ) {

            ParserUtils.advance(builder);
            ParserUtils.skipNLS(builder);

            if ( builder.getTokenType() == kRANGE ) {
                ParserUtils.getToken(builder, kRANGE);
                parser.parseExpression(builder, true, false);

                m.drop();
                return true;
            }
        }

        m.rollbackTo();
        return false;
    }
}
