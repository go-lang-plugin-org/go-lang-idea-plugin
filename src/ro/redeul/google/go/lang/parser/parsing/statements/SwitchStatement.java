package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

import static ro.redeul.google.go.lang.parser.GoParser.ParsingFlag.AllowCompositeLiteral;

/**
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 8:01:22 PM
 */
class SwitchStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!ParserUtils.lookAhead(builder, kSWITCH))
            return null;

        PsiBuilder.Marker marker = builder.mark();

        ParserUtils.getToken(builder, kSWITCH);

        boolean allowCompositeLiteral =
            parser.resetFlag(AllowCompositeLiteral, false);

        boolean isTypeSwitch;

        PsiBuilder.Marker mark = builder.mark();

        IElementType statementType = parser.parseStatementSimple(builder);
        if ( statementType != null && ParserUtils.lookAhead(builder, GoTokenTypeSets.EOS)) {
            ParserUtils.endStatement(builder);
            mark.drop();
        } else {
            mark.rollbackTo();
        }

        isTypeSwitch = tryParseTypeSwitchGuard(builder, parser);

        if ( !isTypeSwitch ) {
            parser.parseExpression(builder);
        }

        ParserUtils.getToken(builder, pLCURLY, "open.curly.expected");

        while (!builder.eof() && builder.getTokenType() != pRCURLY) {

            PsiBuilder.Marker caseMark = builder.mark();

            if (ParserUtils.getToken(builder, kCASE)) {
                if (isTypeSwitch) {
                    parser.parseTypeList(builder);
                } else {
                    parser.parseExpressionList(builder);
                }

                ParserUtils.getToken(builder, oCOLON, "colon.expected");
            } else if (ParserUtils.getToken(builder, kDEFAULT)) {
                ParserUtils.getToken(builder, oCOLON, "colon.expected");
            } else {
                ParserUtils.wrapError(builder, GoBundle.message("case.of.default.keyword.expected"));
            }

            parser.resetFlag(AllowCompositeLiteral, true);
            while (!builder.eof() && !ParserUtils.lookAhead(builder, pRCURLY) && parser.parseStatement(builder) != null) {
                ParserUtils.endStatement(builder);
            }

            caseMark.done(isTypeSwitch ? SWITCH_TYPE_CASE : SWITCH_EXPR_CASE);
        }

        ParserUtils.getToken(builder, pRCURLY, "closed.curly.expected");
        IElementType switchType = isTypeSwitch ? SWITCH_TYPE_STATEMENT : SWITCH_EXPR_STATEMENT;
        marker.done(switchType);

        parser.resetFlag(AllowCompositeLiteral, allowCompositeLiteral);
        return switchType;
    }

    private static boolean tryParseSimpleStmt(PsiBuilder builder,
                                              GoParser parser) {
        PsiBuilder.Marker rememberMarker = builder.mark();

        int expressionCount = parser.parseExpressionList(builder);

        // parse assign expression
        if (expressionCount >= 1 &&
            (
                GoTokenTypeSets.ASSIGN_OPERATORS
                               .contains(builder.getTokenType()) ||
                    GoTokenTypeSets.INC_DEC_OPERATORS
                                   .contains(builder.getTokenType()) ||
                    oVAR_ASSIGN == builder.getTokenType() ||
                    oSEMI == builder.getTokenType()
            )) {
            rememberMarker.rollbackTo();
            parser.parseStatementSimple(builder);
            ParserUtils.getToken(builder, oSEMI);
            return true;
        } else {
            rememberMarker.drop();
            return false;
        }
    }

    static private boolean tryParseTypeSwitchGuard(PsiBuilder builder,
                                                   GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if (ParserUtils.lookAhead(builder, mIDENT, oVAR_ASSIGN)) {
            ParserUtils.eatElement(builder, LITERAL_IDENTIFIER);
            ParserUtils.getToken(builder, oVAR_ASSIGN);
        }

        if (!parser.parsePrimaryExpression(builder)) {
            marker.rollbackTo();
            return false;
        }

        if (!ParserUtils.lookAhead(builder, oDOT, pLPAREN, kTYPE, pRPAREN)) {
            marker.rollbackTo();
            return false;
        }

        ParserUtils.getToken(builder, oDOT);
        ParserUtils.getToken(builder, pLPAREN);
        ParserUtils.getToken(builder, kTYPE);
        ParserUtils.getToken(builder, pRPAREN);

        marker.done(SWITCH_TYPE_GUARD);
        return true;
    }
}
