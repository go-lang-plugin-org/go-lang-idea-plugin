package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;

import static ro.redeul.google.go.lang.parser.GoParser.ParsingFlag.AllowCompositeLiteral;
import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.*;

/**
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 8:01:22 PM
 */
class SwitchStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!lookAhead(builder, kSWITCH))
            return null;

        PsiBuilder.Marker marker = builder.mark();

        getToken(builder, kSWITCH);

        boolean allowCompositeLiteral =
            parser.resetFlag(AllowCompositeLiteral, false);

        boolean isTypeSwitch;

        PsiBuilder.Marker mark = builder.mark();

        IElementType statementType = Statements.parseSimple(builder, parser, false);
        if ( statementType != null && lookAhead(builder, GoTokenTypeSets.EOS)) {
            endStatement(builder);
            mark.drop();
        } else {
            mark.rollbackTo();
        }

        isTypeSwitch = tryParseTypeSwitchGuard(builder, parser);

        if ( !isTypeSwitch ) {
            parser.parseExpression(builder);
        }

        getToken(builder, pLCURLY, "open.curly.expected");

        while (!builder.eof() && builder.getTokenType() != pRCURLY) {

            PsiBuilder.Marker caseMark = builder.mark();

            if (getToken(builder, kCASE)) {
                if (isTypeSwitch) {
                    parser.parseTypeList(builder);
                } else {
                    parser.parseExpressionList(builder);
                }

                getToken(builder, oCOLON, "colon.expected");
            } else if (getToken(builder, kDEFAULT)) {
                getToken(builder, oCOLON, "colon.expected");
            } else {
                wrapError(builder, GoBundle.message("case.of.default.keyword.expected"));
            }

            parser.resetFlag(AllowCompositeLiteral, true);
            // HACK: this should check against the possiblity of infinite loops
            while (!builder.eof() && !lookAhead(builder, pRCURLY) && parser.parseStatement(builder) != null);

            caseMark.done(isTypeSwitch ? SWITCH_TYPE_CASE : SWITCH_EXPR_CASE);
        }

        getToken(builder, pRCURLY, "closed.curly.expected");
        parser.resetFlag(AllowCompositeLiteral, allowCompositeLiteral);

        IElementType switchType = isTypeSwitch ? SWITCH_TYPE_STATEMENT : SWITCH_EXPR_STATEMENT;
        return completeStatement(builder, marker, switchType);
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
                    GoTokenTypeSets.INC_DEC_OPS
                                   .contains(builder.getTokenType()) ||
                    oVAR_ASSIGN == builder.getTokenType() ||
                    oSEMI == builder.getTokenType()
            )) {
            rememberMarker.rollbackTo();
            Statements.parseSimple(builder, parser, false);
            getToken(builder, oSEMI);
            return true;
        } else {
            rememberMarker.drop();
            return false;
        }
    }

    static private boolean tryParseTypeSwitchGuard(PsiBuilder builder,
                                                   GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if (lookAhead(builder, mIDENT, oVAR_ASSIGN)) {
            eatElement(builder, LITERAL_IDENTIFIER);
            getToken(builder, oVAR_ASSIGN);
        }

        if (!parser.parsePrimaryExpression(builder)) {
            marker.rollbackTo();
            return false;
        }

        if (!lookAhead(builder, oDOT, pLPAREN, kTYPE, pRPAREN)) {
            marker.rollbackTo();
            return false;
        }

        getToken(builder, oDOT);
        getToken(builder, pLPAREN);
        getToken(builder, kTYPE);
        getToken(builder, pRPAREN);

        marker.done(SWITCH_TYPE_GUARD);
        return true;
    }
}
