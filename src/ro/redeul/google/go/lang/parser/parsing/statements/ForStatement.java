package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 25, 2010
 * Time: 8:01:22 PM
 */
public class ForStatement implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {
    
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kFOR)) {
            marker.rollbackTo();
            return false;
        }

        ParserUtils.skipNLS(builder);

        if ( builder.getTokenType() != pLCURCLY ) {
            parseConditionOrForClauseOrRangeClause(builder, parser);

        }

        parser.parseBody(builder);

        marker.done(FOR_STATEMENT);
        return true;

    }

    private static void parseConditionOrForClauseOrRangeClause(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker clause = builder.mark();
        parser.parseExpression(builder, true);
        if ( pLCURCLY == builder.getTokenType() ) {
            clause.done(FOR_STATEMENT_CONDITION_CLAUSE);
            return;
        }

        clause.rollbackTo();
        clause = builder.mark();
        
        if ( tryParseRangeClause(builder, parser) ) {
            clause.drop();
            return;
        }

        parser.parseStatementSimple(builder, true);
        ParserUtils.getToken(builder, oSEMI, "semicolon.expected");

        ParserUtils.skipNLS(builder);
        parser.parseExpression(builder, true);
        ParserUtils.getToken(builder, oSEMI, "semicolon.expected");

        ParserUtils.skipNLS(builder);
        parser.parseStatementSimple(builder, true);
        clause.done(FOR_STATEMENT_FOR_CLAUSE);
    }

    private static boolean tryParseRangeClause(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker m = builder.mark();

        parser.parseExpressionList(builder, true);

        if ( builder.getTokenType() == oVAR_ASSIGN ) {

            ParserUtils.advance(builder);
            ParserUtils.skipNLS(builder);

            if ( builder.getTokenType() == kRANGE ) {
                ParserUtils.getToken(builder, kRANGE);

                parser.parseExpression(builder, true);

                m.done(FOR_STATEMENT_RANGE_CLAUSE);
                return true;
            }
        }

        m.rollbackTo();
        return false;
    }
}
