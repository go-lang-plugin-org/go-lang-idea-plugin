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
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 8:01:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class SwitchStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!ParserUtils.lookAhead(builder, kSWITCH))
            return null;

        PsiBuilder.Marker marker = builder.mark();

        ParserUtils.getToken(builder, kSWITCH);

        boolean allowCompositeLiteral =
            parser.resetFlag(AllowCompositeLiteral, false);

        boolean isTypeSwitch = false;
        boolean simpleStatementParsed = false;

        ParserUtils.skipNLS(builder);

        if (ParserUtils.getToken(builder, oSEMI)) {
            simpleStatementParsed = true;
        }

        if (tryParseTypeSwitchGuard(builder, parser)) {
            isTypeSwitch = true;
        } else if (!simpleStatementParsed) {
            parser.tryParseSimpleStmt(builder);
            ParserUtils.getToken(builder, oSEMI);
        }

        ParserUtils.skipNLS(builder);
        if (builder.getTokenType() != pLCURCLY) {
            if (!isTypeSwitch) {
                if (tryParseTypeSwitchGuard(builder, parser)) {
                    isTypeSwitch = true;
                } else {
                    parser.parseExpression(builder);
                }
            }
        }

        ParserUtils.skipNLS(builder);
        ParserUtils.getToken(builder, pLCURCLY, "open.curly.expected");

        while (!builder.eof() && builder.getTokenType() != pRCURLY) {

            ParserUtils.skipNLS(builder);

            PsiBuilder.Marker caseMark = builder.mark();

            int position = builder.getCurrentOffset();
            if (builder.getTokenType() == kCASE) {
                ParserUtils.advance(builder);
                if (isTypeSwitch) {
                    parser.parseTypeList(builder);
                } else {
                    parser.parseExpressionList(builder);
                }
                ParserUtils.getToken(builder, oCOLON, "colon.expected");
            } else if (builder.getTokenType() == kDEFAULT) {
                ParserUtils.advance(builder);
                ParserUtils.getToken(builder, oCOLON, "colon.expected");
            } else {
                ParserUtils.wrapError(builder, GoBundle.message("case.of.default.keyword.expected"));
            }

            parser.resetFlag(AllowCompositeLiteral, true);
            ParserUtils.skipNLS(builder);
            while (builder.getTokenType() != kCASE && builder.getTokenType() != kDEFAULT && builder
                .getTokenType() != pRCURLY) {
                if (parser.parseStatement(builder) == null) {
                    break;
                }
                ParserUtils.skipNLS(builder);
            }

            caseMark.done(isTypeSwitch ? SWITCH_TYPE_CASE : SWITCH_EXPR_CASE);

            ParserUtils.skipNLS(builder);

            if (builder.getCurrentOffset() == position) {
                builder.advanceLexer();
            }
        }

        ParserUtils.getToken(builder, pRCURLY, "closed.curly.expected");
        IElementType switchType = isTypeSwitch ? SWITCH_TYPE_STATEMENT : SWITCH_EXPR_STATEMENT;
        marker.done(switchType);

        parser.resetFlag(AllowCompositeLiteral, allowCompositeLiteral);
        ParserUtils.skipNLS(builder);
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
            ParserUtils.getToken(builder, mIDENT);
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
