package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespacesAndCommentsBinder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.declarations.Declaration;
import ro.redeul.google.go.lang.parser.parsing.helpers.Fragments;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

public class Statements implements GoElementTypes {

    private static final TokenSet SIMPLE_STMT = TokenSet.create(
        mIDENT, litINT, litOCT, litHEX, litCHAR, litFLOAT, litDECIMAL_I, litFLOAT_I, litSTRING,
        kFUNC, kSTRUCT,
        pLPAREN, pLBRACK,
        oMUL, oBIT_AND, oSEND_CHANNEL, oPLUS, oMINUS, oBIT_XOR
    );

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (builder.getTokenType() == kVAR || builder.getTokenType() == kCONST || builder.getTokenType() == kTYPE) {
            return Declaration.parse(builder, parser);
        }

        if (ParserUtils.lookAhead(builder, mIDENT, oCOLON))
            return parseLabeledStatement(builder, parser);

        if (SIMPLE_STMT.contains(builder.getTokenType()))
            return parseSimple(builder, parser, true);

        if (builder.getTokenType() == kGOTO)
            return parseGoToStatement(builder, parser);

        if (builder.getTokenType() == pLCURLY)
            return parseBlockStatement(builder, parser);

        if (builder.getTokenType() == kGO)
            return parseGoStatement(builder, parser);

        if (builder.getTokenType() == kDEFER)
            return parseDeferStatement(builder, parser);

        if (builder.getTokenType() == kRETURN)
        return parseReturnStatement(builder, parser);

        if (builder.getTokenType() == kBREAK)
            return parseBreakStatement(builder, parser);

        if (builder.getTokenType() == kCONTINUE)
            return parseContinueStatement(builder, parser);

        if (builder.getTokenType() == kFALLTHROUGH)
            return parseFallthroughStatement(builder, parser);

        if (builder.getTokenType() == kIF) {
            return IfStatement.parse(builder, parser);
        }

        if (builder.getTokenType() == kSWITCH) {
            return SwitchStatement.parse(builder, parser);
        }

        if (builder.getTokenType() == kSELECT) {
            return SelectStatement.parse(builder, parser);
        }

        if (builder.getTokenType() == kFOR) {
            return ForStatement.parse(builder, parser);
        }

        if (ParserUtils.lookAhead(builder, TokenSet.create(oSEMI, pLCURLY, pRCURLY))) {
            builder.mark().done(EMPTY_STATEMENT);
            ParserUtils.getToken(builder, oSEMI);
            return EMPTY_STATEMENT;
        }

//        builder.error(GoBundle.message("error.statement.expected"));
        return null;
    }

    private static IElementType parseBlockStatement(PsiBuilder builder, GoParser parser) {
        return Fragments.parseBlock(builder, parser, true);
    }

    private static IElementType parseLabeledStatement(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.lookAhead(builder, mIDENT, oCOLON)) {
            marker.rollbackTo();
            return null;
        }

        ParserUtils.eatElement(builder, LITERAL_IDENTIFIER);
        ParserUtils.getToken(builder, oCOLON);

        parser.parseStatement(builder);
        marker.done(LABELED_STATEMENT);
        marker.setCustomEdgeTokenBinders(ParserUtils.CommentBinders.LEADING_COMMENT_GROUP, null);
        return LABELED_STATEMENT;
    }

    private static IElementType parseFallthroughStatement(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kFALLTHROUGH)) {
            marker.rollbackTo();
            return null;
        }

        return completeStatement(builder, marker, FALLTHROUGH_STATEMENT, true);
    }

    private static IElementType parseContinueStatement(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kCONTINUE)) {
            marker.rollbackTo();
            return null;
        }

        if (ParserUtils.lookAhead(builder, mIDENT))
            ParserUtils.eatElement(builder, LITERAL_IDENTIFIER);

        return completeStatement(builder, marker, CONTINUE_STATEMENT, true);
    }

    private static IElementType parseBreakStatement(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kBREAK)) {
            marker.rollbackTo();
            return null;
        }

        if (ParserUtils.lookAhead(builder, mIDENT))
            ParserUtils.eatElement(builder, LITERAL_IDENTIFIER);

        return completeStatement(builder, marker, BREAK_STATEMENT, true);
    }

    private static IElementType parseDeferStatement(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kDEFER)) {
            marker.rollbackTo();
            return null;
        }

        parser.parseExpression(builder);

        return completeStatement(builder, marker, DEFER_STATEMENT, true);
    }

    private static IElementType parseGoStatement(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kGO)) {
            marker.rollbackTo();
            return null;
        }

        parser.parseExpression(builder);

        return completeStatement(builder, marker, GO_STATEMENT, true);
    }

    private static IElementType parseGoToStatement(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kGOTO)) {
            marker.rollbackTo();
            return null;
        }

        PsiBuilder.Marker labelMarker = builder.mark();
        if (!ParserUtils.getToken(builder, mIDENT, "label.expected")) {
            labelMarker.drop();
        } else {
            labelMarker.done(LITERAL_IDENTIFIER);
        }

        return completeStatement(builder, marker, GOTO_STATEMENT, true);
    }

    public static IElementType parseReturnStatement(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kRETURN)) {
            marker.rollbackTo();
            return null;
        }

        if (!ParserUtils.lookAhead(builder, GoTokenTypeSets.EOS)) {
            PsiBuilder.Marker listMarker = builder.mark();

            if (parser.tryParseExpressionList(builder) > 1) {
                listMarker.done(EXPRESSION_LIST);
            } else {
                listMarker.drop();
            }
        }

        return completeStatement(builder, marker, RETURN_STATEMENT, true);
    }

    private static final TokenSet ASSIGN_OPERATORS = TokenSet.create(
        oASSIGN,
        oPLUS_ASSIGN, oMINUS_ASSIGN, oMUL_ASSIGN, oQUOTIENT_ASSIGN, oREMAINDER_ASSIGN,
        oBIT_AND_ASSIGN, oBIT_OR_ASSIGN, oBIT_XOR_ASSIGN, oSHIFT_LEFT_ASSIGN, oSHIFT_RIGHT_ASSIGN,
        oBIT_CLEAR_ASSIGN
    );

    public static boolean tryParseSimple(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker rememberMarker = builder.mark();

        int expressionCount = parser.parseExpressionList(builder);

        // parse assign expression
        if (expressionCount >= 1 &&
            (
                GoTokenTypeSets.ASSIGN_OPERATORS.contains(builder.getTokenType()) ||
                    GoTokenTypeSets.INC_DEC_OPS.contains(builder.getTokenType()) ||
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
        return parseSimple(builder, parser, true);
    }

    public static IElementType parseSimple(PsiBuilder builder, GoParser parser,
                                           boolean completeStatement) {

        PsiBuilder.Marker mark = builder.mark();

        int expressionCount = parser.parseExpressionList(builder);

        if (expressionCount == 0 && ParserUtils.lookAhead(builder, GoTokenTypeSets.EOS))
            return completeStatement(builder, mark, EMPTY_STATEMENT, completeStatement);


        if (ASSIGN_OPERATORS.contains(builder.getTokenType())) {
            mark.done(EXPRESSION_LIST);
            mark = mark.precede();
            PsiBuilder.Marker marker = builder.mark();
            ParserUtils.getToken(builder, builder.getTokenType());

            PsiBuilder.Marker rightSideExpressions = builder.mark();
            if (parser.parseExpressionList(builder) != 0) {
                rightSideExpressions.done(EXPRESSION_LIST);
                marker.drop();
                return completeStatement(builder, mark, ASSIGN_STATEMENT, completeStatement);
            } else {
                rightSideExpressions.drop();
                marker.rollbackTo();
            }
        }

        if (GoTokenTypeSets.INC_DEC_OPS.contains(builder.getTokenType())) {
            ParserUtils.getToken(builder, builder.getTokenType());
            return completeStatement(builder, mark, INC_DEC_STATEMENT, completeStatement);
        }

//        if (ParserUtils.lookAhead(builder, oVAR_ASSIGN)) {
//            ParserUtils.getToken(builder, oVAR_ASSIGN);
//            parser.parseExpressionList(builder);
//            mark.done(SHORT_VAR_STATEMENT);
//            return SHORT_VAR_STATEMENT;
//        }

        if (ParserUtils.lookAhead(builder, oVAR_ASSIGN, kRANGE)) {
            mark.rollbackTo();
            return null;
        }

        if (ParserUtils.lookAhead(builder, oVAR_ASSIGN)) {
            mark.rollbackTo();
            mark = builder.mark();
            parser.parseIdentifierList(builder, false);
            if (!ParserUtils.getToken(builder, oVAR_ASSIGN)) {
                mark.rollbackTo();
                return null;
            }

            if (parser.parseExpressionList(builder) == 0) {
                mark.rollbackTo();
                return null;
            }

            return completeStatement(builder, mark, SHORT_VAR_STATEMENT, completeStatement);
        }

        if (ParserUtils.lookAhead(builder, oSEND_CHANNEL)) {
            ParserUtils.getToken(builder, oSEND_CHANNEL);
            parser.parseExpression(builder);
            return completeStatement(builder, mark, SEND_STATEMENT, completeStatement);
        }

        if (expressionCount == 0 && ParserUtils.lookAhead(builder, pLCURLY))
            return completeStatement(builder, mark, EMPTY_STATEMENT, completeStatement);

        if (expressionCount != 0)
            return completeStatement(builder, mark, EXPRESSION_STATEMENT, completeStatement);

        mark.drop();
        return null;
    }

    private static IElementType completeStatement(PsiBuilder builder, PsiBuilder.Marker mark,
                                               IElementType elementType, boolean completeStatement) {
        if (completeStatement)
            return ParserUtils.completeStatement(builder, mark, elementType);
        else {
            mark.done(elementType);
            return elementType;
        }
    }
}
