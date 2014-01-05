package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.*;

class SelectStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!lookAhead(builder, kSELECT))
            return null;

        PsiBuilder.Marker marker = builder.mark();
        getToken(builder, kSELECT);

        getToken(builder, pLCURLY, "open.curly.expected");

        while ( !builder.eof() && builder.getTokenType() != pRCURLY) {

            PsiBuilder.Marker caseMark = builder.mark();

            boolean validCase = false;
            int position = builder.getCurrentOffset();
            IElementType clauseType = null;
            if (builder.getTokenType() == kCASE) {
                advance(builder);
                clauseType = parseSendOrRecvExpression(builder, parser);
                getToken(builder, oCOLON, "colon.expected");
                validCase = true;
            } else if (builder.getTokenType() == kDEFAULT) {
                advance(builder);
                getToken(builder, oCOLON, "colon.expected");
                clauseType = SELECT_COMM_CLAUSE_DEFAULT;
                validCase = true;
            } else if ( builder.getTokenType() != pRCURLY ) {
                wrapError(builder, "case.of.default.keyword.expected");
            }

            if ( validCase ) {
                while (builder.getTokenType() != kCASE && builder.getTokenType() != kDEFAULT && builder.getTokenType() != pRCURLY) {
                    if (parser.parseStatement(builder) == null) {
                        break;
                    }

                    endStatement(builder);
                }

                caseMark.done(clauseType);
            } else {
                caseMark.drop();
            }

            if (builder.getCurrentOffset() == position) {
                builder.advanceLexer();
            }
        }

        getToken(builder, pRCURLY, "closed.curly.expected");

        return completeStatement(builder, marker, SELECT_STATEMENT);
    }

    private static IElementType parseSendOrRecvExpression(PsiBuilder builder, GoParser parser) {

        // case |<-a:
        if ( oSEND_CHANNEL == builder.getTokenType() ) {
            parser.parseExpression(builder);
            return SELECT_COMM_CLAUSE_RECV;
        }

        PsiBuilder.Marker mark = builder.mark();

        int exprCount = parser.parseExpressionList(builder);

        // case a |<- <expr>:
        if ( oSEND_CHANNEL == builder.getTokenType() ) {
            builder.advanceLexer();
            parser.parseExpression(builder);
            mark.done(SEND_STATEMENT);
            return SELECT_COMM_CLAUSE_SEND;
        }

        // case a(, b)? |(= | :=) <expr>:
        if ( (exprCount == 1 || exprCount == 2) &&
            (oASSIGN == builder.getTokenType() || oVAR_ASSIGN == builder.getTokenType())) {
            builder.advanceLexer();
            mark.drop();
            mark = builder.mark();
            parser.parseExpression(builder);
            mark.done(SELECT_COMM_CLAUSE_RECV_EXPR);
            return SELECT_COMM_CLAUSE_RECV;
        }

        // case expr:
        if ( exprCount == 1 && oCOLON == builder.getTokenType() ) {
            mark.drop();
            return SELECT_COMM_CLAUSE_RECV;
        }

        builder.error("assign.or.varassign.or.send.channel.operator.expected");

        parser.parseExpression(builder);

        mark.done(SELECT_COMM_CLAUSE_RECV_EXPR);
        return SELECT_COMM_CLAUSE_RECV;
    }
}
