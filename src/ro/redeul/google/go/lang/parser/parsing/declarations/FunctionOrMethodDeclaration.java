package ro.redeul.google.go.lang.parser.parsing.declarations;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 25, 2010
 * Time: 12:05:14 AM
 */
public class FunctionOrMethodDeclaration extends ParserUtils
    implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {


        skipNLS(builder);

        if (!ParserUtils.lookAhead(builder, kFUNC))
            return null;

        PsiBuilder.Marker marker = builder.mark();

        ParserUtils.getToken(builder, kFUNC);
        // parse the receiver description
        skipNLS(builder);
        IElementType nodeType = FUNCTION_DECLARATION;
        if (lookAhead(builder, pLPAREN)) {
            parseReceiverDeclaration(builder, parser);
            nodeType = METHOD_DECLARATION;
        }

        // expecting method name
        skipNLS(builder);
        if (parser.isSet(GoParser.ParsingFlag.Debug)) {
            LOG.debug("Method: " + builder.getTokenText());
        }

        getToken(builder, mIDENT,
                 GoBundle.message("error.method.name.expected"));

        skipNLS(builder);
        parseCompleteMethodSignature(builder, parser);

        skipNLS(builder);

        parser.parseBody(builder);

        marker.done(nodeType);
        return nodeType;
    }

    public static boolean parseCompleteMethodSignature(PsiBuilder builder,
                                                       GoParser parser) {
        parseSignature(builder, parser);

        if (builder.getTokenType() == pLPAREN) {

            PsiBuilder.Marker result = builder.mark();
            parseSignature(builder, parser);
            result.done(FUNCTION_RESULT);

        } else if (!builder.eof() && builder.getTokenType() != pLCURCLY) {
            PsiBuilder.Marker result = builder.mark();
            parser.parseType(builder);

            result.done(FUNCTION_PARAMETER);
            result = result.precede();

            result.done(FUNCTION_PARAMETER_LIST);
            result = result.precede();

            result.done(FUNCTION_RESULT);
        }

        return true;
    }

    /**
     * Receiver     := "(" [ identifier ] [ "*" ] BaseTypeName ")" .
     * BaseTypeName := identifier .
     *
     * @param builder
     * @param parser
     */
    private static void parseReceiverDeclaration(PsiBuilder builder,
                                                 GoParser parser) {

        ParserUtils.getToken(builder, pLPAREN, "open.parenthesis.expected");

        PsiBuilder.Marker receiverDeclarationMarker = builder.mark();

        if (ParserUtils.lookAhead(builder, mIDENT,
                                  mIDENT) || ParserUtils.lookAhead(builder,
                                                                   mIDENT,
                                                                   oMUL)) {
            ParserUtils.eatElement(builder, LITERAL_IDENTIFIER);
        }

        ParserUtils.skipNLS(builder);
        parser.parseTypeName(builder);

        ParserUtils.skipNLS(builder);
        ParserUtils.getToken(builder, pRPAREN, "close.parenthesis.expected");

        receiverDeclarationMarker.done(METHOD_RECEIVER);
    }

    public static boolean parseSignature(PsiBuilder builder, GoParser parser) {

        ParserUtils.getToken(builder, pLPAREN, "open.parenthesis.expected");

        ParserUtils.skipNLS(builder);

        if (tryParameterListAsAnonymousTypes(builder, parser))
            return true;

        PsiBuilder.Marker signature = builder.mark();

        while (!builder.eof() && builder.getTokenType() != pRPAREN) {

            boolean isVariadic = false;
            int pos = builder.getCurrentOffset();
            PsiBuilder.Marker parameterSignature = builder.mark();
            parser.parseIdentifierList(builder, false);

            ParserUtils.skipNLS(builder);
            if (builder.getTokenType() == oTRIPLE_DOT) {
                ParserUtils.advance(builder);
                isVariadic = true;
            }

            ParserUtils.skipNLS(builder);
            parser.parseType(builder);

            parameterSignature.done(
                isVariadic ? FUNCTION_PARAMETER_VARIADIC : FUNCTION_PARAMETER);

            ParserUtils.skipNLS(builder);
            if (builder.getTokenType() == oCOMMA) {
                ParserUtils.advance(builder);
                ParserUtils.skipNLS(builder);
            }

            if (pos == builder.getCurrentOffset()) {
                ParserUtils.wrapError(builder, "unexpected.char");
            }
        }

        signature.done(FUNCTION_PARAMETER_LIST);

        ParserUtils.skipNLS(builder);
        ParserUtils.getToken(builder, pRPAREN, "close.parenthesis.expected");


        return true;
    }

    private static boolean tryParameterListAsAnonymousTypes(PsiBuilder builder,
                                                            GoParser parser) {
        PsiBuilder.Marker signature = builder.mark();

        int parameterCount = 0;
        // first try to parse as a list of types
        while (!builder.eof()) {
            PsiBuilder.Marker argument = builder.mark();

            if (builder.getTokenType() == oTRIPLE_DOT) {
                ParserUtils.eatElement(builder, oTRIPLE_DOT);
            }

            ParserUtils.skipNLS(builder);
            if (parser.parseType(builder)) {
                argument.done(FUNCTION_PARAMETER);
                parameterCount++;
            } else {
                argument.drop();
            }

            ParserUtils.skipNLS(builder);
            if (builder.getTokenType() == oCOMMA) {
                builder.advanceLexer();
                ParserUtils.skipNLS(builder);
            } else {
                break;
            }
        }

        if (builder.getTokenType() == pRPAREN) {
            if (parameterCount > 0) {
                signature.done(FUNCTION_PARAMETER_LIST);
            } else {
                signature.drop();
            }

            ParserUtils.advance(builder);
            return true;
        }

        signature.rollbackTo();
        return false;
    }

}
