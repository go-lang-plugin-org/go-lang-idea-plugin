package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.declarations.Declaration;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 7:47:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class Statements implements GoElementTypes {

    static TokenSet SIMPLE_STMT = TokenSet.create(
        mIDENT, litINT, litOCT, litHEX, litCHAR, litFLOAT, litDECIMAL_I, litFLOAT_I, litSTRING,
        kFUNC, kSTRUCT,
        pLPAREN, pLBRACK,
        oMUL, oBIT_AND, oSEND_CHANNEL, oPLUS, oMINUS, oBIT_XOR
    );

    public static boolean parse(PsiBuilder builder, GoParser parser, boolean inControlClause) {
        
        if ( builder.getTokenType() == kVAR || builder.getTokenType() == kCONST || builder.getTokenType() == kTYPE ) {
            return Declaration.parse(builder, parser);
        }

        if ( ParserUtils.lookAhead(builder, mIDENT, oCOLON) ) {
            return LabeledStatement.parse(builder, parser);
        }

        if ( SIMPLE_STMT.contains(builder.getTokenType()) ) {
            return parseSimple(builder, parser, inControlClause);
        }

        if ( builder.getTokenType() == kGO ) {
            return GoStatement.parse(builder, parser);
        }

        if ( builder.getTokenType() == kDEFER ) {
            return DeferStatement.parse(builder, parser);
        }

        if ( builder.getTokenType() == kRETURN ) {
            return ReturnStatement.parse(builder, parser);
        }

        if ( builder.getTokenType() == kBREAK ) {
            return BreakStatement.parse(builder, parser);
        }

        if ( builder.getTokenType() == kCONTINUE ) {
            return ContinueStatement.parse(builder, parser);
        }

        if ( builder.getTokenType() == kFALLTHROUGH ) {
            return FallthroughStatement.parse(builder, parser);
        }

        if ( builder.getTokenType() == kGOTO ) {
            return GotoStatement.parse(builder, parser);
        }

        if ( builder.getTokenType() == pLCURCLY ) {
            return parser.parseBody(builder);
        }

        if ( builder.getTokenType() == kIF ) {
            return IfStatement.parse(builder, parser);
        }

        if ( builder.getTokenType() == kSWITCH ) {
            return SwitchStatement.parse(builder, parser);
        }

        if ( builder.getTokenType() == kSELECT ) {
            return SelectStatement.parse(builder, parser);
        }
        
        if ( builder.getTokenType() == kFOR ) {
            return ForStatement.parse(builder, parser);
        }

        if ( builder.getTokenType() == oSEMI ) {
            ParserUtils.eatElement(builder, STATEMENT);
            return true;
        }

        if ( builder.getTokenType() == pRCURLY) {
//            ParserUtils.eatElement(builder, STATEMENT);
            return true;
        }

        builder.error("statement.expected");
//
//
//        PsiBuilder.Marker marker = builder.mark();
//
//        while ( builder.getTokenType() != oSEMI && ! builder.eof() )
//        {
//            builder.advanceLexer();
//        }
//
//        ParserUtils.getToken(builder, oSEMI);
//
//        marker.done(STATEMENT);
//
        return false;
    }

    static TokenSet ASSIGN_OPERATORS = TokenSet.create(
            oASSIGN,
            oPLUS_ASSIGN, oMINUS_ASSIGN, oMUL_ASSIGN, oQUOTIENT_ASSIGN, oREMAINDER_ASSIGN,
            oBIT_AND_ASSIGN, oBIT_OR_ASSIGN, oBIT_XOR_ASSIGN, oSHIFT_LEFT_ASSIGN, oSHIFT_RIGHT_ASSIGN,
            oBIT_CLEAR_ASSIGN
    );

    public static boolean tryParseSimple(PsiBuilder builder, GoParser parser, boolean inControlClause) {
        PsiBuilder.Marker rememberMarker = builder.mark();

        int expressionCount = parser.parseExpressionList(builder, true);

        // parse assign expression
        if (expressionCount >= 1 &&
                (
                        GoTokenTypeSets.ASSIGN_OPERATORS.contains(builder.getTokenType()) ||
                                GoTokenTypeSets.INC_DEC_OPERATORS.contains(builder.getTokenType()) ||
                                oVAR_ASSIGN == builder.getTokenType() ||
                                oSEMI == builder.getTokenType()
                )) {
            rememberMarker.rollbackTo();
            parseSimple(builder, parser, inControlClause);            
            ParserUtils.getToken(builder, oSEMI);
            return true;
        } else {
            rememberMarker.drop();
            return false;
        }
    }

    public static boolean parseSimple(PsiBuilder builder, GoParser parser, boolean inControlClause) {

        PsiBuilder.Marker mark = builder.mark();

        int expressionCount = parser.parseExpressionList(builder, inControlClause);

        if ( ASSIGN_OPERATORS.contains(builder.getTokenType()) ) {
            ParserUtils.getToken(builder, builder.getTokenType());

            ParserUtils.skipNLS(builder);

            parser.parseExpressionList(builder, inControlClause);

            mark.done(ASSIGN_STATEMENT);
            return true;
        }

        if ( oMINUS_MINUS == builder.getTokenType() || oPLUS_PLUS == builder.getTokenType() ) {
            ParserUtils.getToken(builder, builder.getTokenType());

            mark.done(INC_DEC_STATEMENT);
            return true;            
        }

        if  (oVAR_ASSIGN == builder.getTokenType() ) {
            mark.rollbackTo();

            mark = builder.mark();
            parser.parseIdentifierList(builder);
            ParserUtils.getToken(builder, oVAR_ASSIGN, "assignment.operator.expected");

            ParserUtils.skipNLS(builder);
            parser.parseExpressionList(builder, inControlClause);
            mark.done(SHORT_VAR_STATEMENT);
            return true;
        }

        if ( expressionCount == 0 ) {
            mark.done(EMPTY_STATEMENT);
            return true;
        }

        mark.done(EXPRESSION_STATEMENT);
        return true;
    }
}
