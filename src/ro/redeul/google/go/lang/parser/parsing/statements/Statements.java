package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.declarations.Declaration;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.completeStatement;

public class Statements implements GoElementTypes {

    private static final TokenSet SIMPLE_STMT = TokenSet.create(
        mIDENT, litINT, litOCT, litHEX, litCHAR, litFLOAT, litDECIMAL_I, litFLOAT_I, litSTRING,
        kFUNC, kSTRUCT,
        pLPAREN, pLBRACK,
        oMUL, oBIT_AND, oSEND_CHANNEL, oPLUS, oMINUS, oBIT_XOR
    );

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if ( builder.getTokenType() == kVAR || builder.getTokenType() == kCONST || builder.getTokenType() == kTYPE ) {
            return Declaration.parse(builder, parser);
        }

        if ( ParserUtils.lookAhead(builder, mIDENT, oCOLON) ) {
            return LabeledStatement.parse(builder, parser);
        }

        if ( SIMPLE_STMT.contains(builder.getTokenType()) ) {
            return parseSimple(builder, parser);
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
            return BreakStatement.parse(builder);
        }

        if ( builder.getTokenType() == kCONTINUE ) {
            return ContinueStatement.parse(builder);
        }

        if ( builder.getTokenType() == kFALLTHROUGH ) {
            return FallthroughStatement.parse(builder);
        }

        if ( builder.getTokenType() == kGOTO ) {
            return GotoStatement.parse(builder);
        }

        if ( builder.getTokenType() == pLCURLY) {
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

        if ( ParserUtils.lookAhead(builder, TokenSet.create(oSEMI, pLCURLY, pRCURLY)) ) {
            builder.mark().done(EMPTY_STATEMENT);
            ParserUtils.getToken(builder, oSEMI);
            return EMPTY_STATEMENT;
        }

//        builder.error(GoBundle.message("error.statement.expected"));
        return null;
    }

    private static final TokenSet ASSIGN_OPERATORS = TokenSet.create(
            oASSIGN,
            oPLUS_ASSIGN, oMINUS_ASSIGN, oMUL_ASSIGN, oQUOTIENT_ASSIGN, oREMAINDER_ASSIGN,
            oBIT_AND_ASSIGN, oBIT_OR_ASSIGN, oBIT_XOR_ASSIGN, oSHIFT_LEFT_ASSIGN, oSHIFT_RIGHT_ASSIGN,
            oBIT_CLEAR_ASSIGN
    );

    private static final TokenSet INC_DEC_OPERATORS = TokenSet.create(
        oMINUS_MINUS, oPLUS_PLUS
    );

    public static boolean tryParseSimple(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker rememberMarker = builder.mark();

        int expressionCount = parser.parseExpressionList(builder);

        // parse assign expression
        if (expressionCount >= 1 &&
                (
                        GoTokenTypeSets.ASSIGN_OPERATORS.contains(builder.getTokenType()) ||
                                GoTokenTypeSets.INC_DEC_OPERATORS.contains(builder.getTokenType()) ||
                                oVAR_ASSIGN == builder.getTokenType() ||
                                oSEMI == builder.getTokenType()
                )) {
            rememberMarker.rollbackTo();
            parseSimple(builder, parser);
 //           ParserUtils.getToken(builder, oSEMI);
            return true;
        } else {
            rememberMarker.drop();
            return false;
        }
    }

    public static IElementType parseSimple(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker mark = builder.mark();

        int expressionCount = parser.parseExpressionList(builder);

        if ( expressionCount == 0 && ParserUtils.lookAhead(builder, GoTokenTypeSets.EOS) )
            return completeStatement(builder, mark, EMPTY_STATEMENT);

        if ( ASSIGN_OPERATORS.contains(builder.getTokenType()) ) {
            mark.done(EXPRESSION_LIST);
            mark = mark.precede();
            PsiBuilder.Marker marker = builder.mark();
            ParserUtils.getToken(builder, builder.getTokenType());

            PsiBuilder.Marker rightSideExpressions = builder.mark();
            if ( parser.parseExpressionList(builder) != 0) {
                rightSideExpressions.done(EXPRESSION_LIST);
                marker.drop();
                return completeStatement(builder, mark, ASSIGN_STATEMENT);
            } else {
                rightSideExpressions.drop();
                marker.rollbackTo();
            }
        }

        if ( INC_DEC_OPERATORS.contains(builder.getTokenType())) {
            ParserUtils.getToken(builder, builder.getTokenType());
            return completeStatement(builder, mark, INC_DEC_STATEMENT);
        }

//        if (ParserUtils.lookAhead(builder, oVAR_ASSIGN)) {
//            ParserUtils.getToken(builder, oVAR_ASSIGN);
//            parser.parseExpressionList(builder);
//            mark.done(SHORT_VAR_STATEMENT);
//            return SHORT_VAR_STATEMENT;
//        }

        if  (ParserUtils.lookAhead(builder, oVAR_ASSIGN, kRANGE)) {
            mark.rollbackTo();
            return null;
        }

        if (ParserUtils.lookAhead(builder, oVAR_ASSIGN)) {
            mark.rollbackTo();
            mark = builder.mark();
            parser.parseIdentifierList(builder, false);
            if ( ! ParserUtils.getToken(builder, oVAR_ASSIGN) ) {
                mark.rollbackTo();
                return null;
            }

            if ( parser.parseExpressionList(builder) == 0 ) {
                mark.rollbackTo();
                return null;
            }

            return completeStatement(builder, mark, SHORT_VAR_STATEMENT);
        }

        if (ParserUtils.lookAhead(builder, oSEND_CHANNEL)) {
            ParserUtils.getToken(builder, oSEND_CHANNEL);
            parser.parseExpression(builder);
            return completeStatement(builder, mark, SEND_STATEMENT);
        }

        if ( expressionCount == 0 && ParserUtils.lookAhead(builder, pLCURLY))
            return completeStatement(builder, mark, EMPTY_STATEMENT);

        if ( expressionCount != 0 )
            return completeStatement(builder, mark, EXPRESSION_STATEMENT);

        mark.drop();
        return null;
    }
}
