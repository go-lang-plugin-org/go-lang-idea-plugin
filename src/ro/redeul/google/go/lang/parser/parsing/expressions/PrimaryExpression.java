package ro.redeul.google.go.lang.parser.parsing.expressions;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.types.ArrayType;
import ro.redeul.google.go.lang.parser.parsing.types.InterfaceType;
import ro.redeul.google.go.lang.parser.parsing.types.MapType;
import ro.redeul.google.go.lang.parser.parsing.types.SliceType;
import ro.redeul.google.go.lang.parser.parsing.types.StructType;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

public class PrimaryExpression implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser,
                                boolean inControlExpressions, boolean parseIota) {

        ParserUtils.skipNLS(builder);

        int position = builder.getCurrentOffset();
        PsiBuilder.Marker mark = builder.mark();
        parseOperand(builder, parser, parseIota);
        // if we have a basic literal operand

        boolean keepParsing;
        do {
            if ( builder.getTokenType() == oDOT ) {
                keepParsing = parseSelectorOrTypeAssertion(builder, parser, mark);
            } else if ( builder.getTokenType() == pLPAREN ) {
                keepParsing = parseCallOrConversion(builder, parser, mark, parseIota);
            } else if ( builder.getTokenType() == pLBRACK ) {
                keepParsing = parseIndexOrSlice(builder, parser, mark);
            } else if ( builder.getTokenType() == pLCURCLY && ! inControlExpressions ) {
                keepParsing = parseCompositeLiteral(builder, parser, mark, parseIota);
            } else {
                break;
            }

            if ( keepParsing ) {
                mark = mark.precede();
            }

        } while ( ! builder.eof() && keepParsing );

        mark.drop();

        return position != builder.getCurrentOffset();
    }

    private static boolean parseIndexOrSlice(PsiBuilder builder, GoParser parser, PsiBuilder.Marker mark) {

        ParserUtils.getToken(builder, pLBRACK);
        ParserUtils.skipNLS(builder);

        parser.parseExpression(builder, false, false);
        ParserUtils.skipNLS(builder);

        boolean isSlice = false;
        if (builder.getTokenType() == oCOLON ) {
            builder.advanceLexer();
            isSlice = true;

            parser.parseExpression(builder, false, false);
            ParserUtils.skipNLS(builder);
        }

        ParserUtils.getToken(builder, pRBRACK, "right.bracket.expected");

        mark.done( isSlice ? SLICE_EXPRESSION : INDEX_EXPRESSION);

        return true;
    }

    private static boolean parseCompositeLiteral(PsiBuilder builder, GoParser parser,
                                                 PsiBuilder.Marker mark, boolean parseIota) {

        PsiBuilder.Marker marker = builder.mark();

        ParserUtils.getToken(builder, pLCURCLY);
        ParserUtils.skipNLS(builder);

        while ( ! builder.eof() && builder.getTokenType() != pRCURLY ) {

            PsiBuilder.Marker elementMarker = builder.mark();

            PsiBuilder.Marker keyOrValueExpression = builder.mark();

            if ( ! parser.parseExpression(builder, false, parseIota) ) {
                ParserUtils.wrapError(builder, "expression.expected");
            }

            if ( builder.getTokenType() == oCOLON ) {
                keyOrValueExpression.done(COMPOSITE_LITERAL_ELEMENT_KEY);
                builder.advanceLexer();
                ParserUtils.skipNLS(builder);

                keyOrValueExpression = builder.mark();
                parser.parseExpression(builder, false, parseIota);
            }

            keyOrValueExpression.done(COMPOSITE_LITERAL_ELEMENT_VALUE);

            elementMarker.done(COMPOSITE_LITERAL_ELEMENT);
            if ( builder.getTokenType() != pRCURLY ) {
                ParserUtils.getToken(builder, oCOMMA, "comma.expected");
            }

            ParserUtils.skipNLS(builder);
        }

        ParserUtils.getToken(builder, pRCURLY, "closed.parenthesis.expected");

        marker.done(COMPOSITE_LITERAL_ELEMENT_LIST);
        mark.done(COMPOSITE_LITERAL_EXPRESSION);
        return true;
    }

    private static boolean parseCallOrConversion(PsiBuilder builder, GoParser parser,
                                                 PsiBuilder.Marker mark,
                                                 boolean parseIota) {

        ParserUtils.getToken(builder, pLPAREN);

        if ( builder.getTokenType() != pRPAREN ) {
            parser.parseExpressionList(builder, false, parseIota);
        }

        ParserUtils.getToken(builder, pRPAREN, "closed.parenthesis.expected");

        mark.done(CALL_OR_CONVERSION_EXPRESSION);
        return true;
    }

    private static boolean parseSelectorOrTypeAssertion(PsiBuilder builder, GoParser parser, PsiBuilder.Marker mark) {

        PsiBuilder.Marker rollBackMarker = builder.mark();

        ParserUtils.getToken(builder, oDOT);

        ParserUtils.skipNLS(builder);

        if ( mIDENT == builder.getTokenType() ) {
            ParserUtils.getToken(builder, mIDENT);
            rollBackMarker.drop();
            mark.done(SELECTOR_EXPRESSION);
            return true;
        }

        ParserUtils.skipNLS(builder);
        ParserUtils.getToken(builder, pLPAREN, "open.parenthesis.expected");

        ParserUtils.skipNLS(builder);
        if ( kTYPE == builder.getTokenType() ) {
            rollBackMarker.rollbackTo();
            return false;
        }

        parser.parseType(builder);

        ParserUtils.getToken(builder, pRPAREN, "closed.parenthesis.expected");
        rollBackMarker.drop();
        mark.done(TYPE_ASSERTION_EXPRESSION);
        return true;
    }

    private static boolean parseOperand(PsiBuilder builder, GoParser parser,
                                        boolean parseIota) {

        if ( ParserUtils.lookAhead(builder, mIDENT, pLPAREN) && BuiltInCallExpression.isBuiltInCall(builder.getTokenText() ) )
            return BuiltInCallExpression.parse(builder, parser);

        if ( GoTokenTypeSets.LITERALS.contains(builder.getTokenType()) )
            return LiteralExpression.parse(builder, parser, parseIota);

        if ( pLPAREN == builder.getTokenType() ) {
            return ParenthesizedExpression.parse(builder, parser, parseIota);
        }

        if ( kFUNC == builder.getTokenType() ) {
            return parseFunctionTypeOrLiteral(builder, parser);
        }

        if ( kMAP == builder.getTokenType() ) {
            return MapType.parse(builder, parser);
        }

        if ( kSTRUCT == builder.getTokenType() ) {
            return StructType.parse(builder, parser);
        }

        if ( kINTERFACE == builder.getTokenType() ) {
            return InterfaceType.parse(builder, parser);
        }

        if ( pLBRACK == builder.getTokenType() ) {
            PsiBuilder.Marker marker = builder.mark();
            ParserUtils.advance(builder);
            ParserUtils.skipNLS(builder);
            if ( pRBRACK == builder.getTokenType() ) {
                marker.rollbackTo();
                SliceType.parse(builder, parser);
            } else {
                marker.rollbackTo();
                ArrayType.parse(builder, parser);
            }
        }

        return false;
    }

    private static boolean parseFunctionTypeOrLiteral(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker mark = builder.mark();

        parser.parseType(builder);

        PsiBuilder.Marker mark2 = builder.mark();

        ParserUtils.skipNLS(builder);

        if ( pLCURCLY == builder.getTokenType() ) {
            parser.parseBody(builder);

            mark2.drop();
            mark.done(FUNCTION_LITERAL_EXPRESSION);
        } else {
            mark2.rollbackTo();
            mark.drop();
        }

        return true;
    }

}
