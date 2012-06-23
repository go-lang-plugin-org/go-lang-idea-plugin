package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

public class SelectStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!ParserUtils.lookAhead(builder, kSELECT))
            return null;

        PsiBuilder.Marker marker = builder.mark();
        ParserUtils.getToken(builder, kSELECT);

        ParserUtils.skipNLS(builder);
        ParserUtils.getToken(builder, pLCURCLY, "open.curly.expected");
        ParserUtils.skipNLS(builder);

        while ( !builder.eof() && builder.getTokenType() != pRCURLY) {
            ParserUtils.skipNLS(builder);

            PsiBuilder.Marker caseMark = builder.mark();

            boolean validCase = false;
            int position = builder.getCurrentOffset();
            if (builder.getTokenType() == kCASE) {
                ParserUtils.advance(builder);
                parseSendOrRecvExpression(builder, parser);

                ParserUtils.getToken(builder, oCOLON, "colon.expected");
                validCase = true;
            } else if (builder.getTokenType() == kDEFAULT) {
                ParserUtils.advance(builder);
                ParserUtils.getToken(builder, oCOLON, "colon.expected");
                validCase = true;
            } else if ( builder.getTokenType() != pRCURLY ) {
                ParserUtils.wrapError(builder, "case.of.default.keyword.expected");
            }

            ParserUtils.skipNLS(builder);
            if ( validCase ) {
                while (builder.getTokenType() != kCASE && builder.getTokenType() != kDEFAULT && builder.getTokenType() != pRCURLY) {
                    if (parser.parseStatement(builder) == null) {
                        break;
                    }
                    ParserUtils.skipNLS(builder);
                }

                caseMark.done(SELECT_CASE);
            } else {
                caseMark.drop();
            }

            ParserUtils.skipNLS(builder);

            if (builder.getCurrentOffset() == position) {
                builder.advanceLexer();
            }

        }

        ParserUtils.getToken(builder, pRCURLY, "closed.curly.expected");
        marker.done(SELECT_STATEMENT);
        ParserUtils.skipNLS(builder);
        return SELECT_STATEMENT;
    }

    private static void parseSendOrRecvExpression(PsiBuilder builder, GoParser parser) {
        ParserUtils.skipNLS(builder);

        if ( oSEND_CHANNEL == builder.getTokenType() ) {

            PsiBuilder.Marker marker = builder.mark();
            builder.advanceLexer();

            ParserUtils.skipNLS(builder);
            parser.parseExpression(builder);
            marker.done(SELECT_CASE_RECV_EXPRESSION);
            return;
        }

        PsiBuilder.Marker mark = builder.mark();

        parser.parseExpression(builder);
        if ( oSEND_CHANNEL == builder.getTokenType() ) {
            builder.advanceLexer();
            ParserUtils.skipNLS(builder);
            parser.parseExpression(builder);
            mark.done(SELECT_CASE_SEND_EXPRESSION);
            return;
        }

        if ( oASSIGN == builder.getTokenType() || oVAR_ASSIGN == builder.getTokenType() ) {
            builder.advanceLexer();

            ParserUtils.skipNLS(builder);
            ParserUtils.getToken(builder, oSEND_CHANNEL, "send.channel.operator.expected");

            ParserUtils.skipNLS(builder);
            parser.parseExpression(builder);

            mark.done(SELECT_CASE_RECV_EXPRESSION);
            return;
        }

        if ( oCOLON == builder.getTokenType() ) {
            mark.done(SELECT_CASE_SEND_EXPRESSION);
            return;
        }

        builder.error("assign.or.varassign.or.send.channel.operator.expected");

        parser.parseExpression(builder);

        mark.done(SELECT_CASE_SEND_EXPRESSION);
    }

}
