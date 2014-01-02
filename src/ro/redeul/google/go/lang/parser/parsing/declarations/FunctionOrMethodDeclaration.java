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

        if (!ParserUtils.lookAhead(builder, kFUNC))
            return null;

        PsiBuilder.Marker marker = builder.mark();

        ParserUtils.getToken(builder, kFUNC);
        // parse the receiver description
        IElementType nodeType = FUNCTION_DECLARATION;
        if (lookAhead(builder, pLPAREN)) {
            parseReceiverDeclaration(builder, parser);
            nodeType = METHOD_DECLARATION;
        }

        // expecting method name
        if (parser.isSet(GoParser.ParsingFlag.Debug)) {
            LOG.debug("Method: " + builder.getTokenText());
        }

        if (ParserUtils.lookAhead(builder, mIDENT)) {
            ParserUtils.eatElement(builder, LITERAL_IDENTIFIER);
            parseCompleteMethodSignature(builder, parser);
        } else {
            builder.error(GoBundle.message("error.method.name.expected"));
        }

        if (ParserUtils.lookAhead(builder, pLCURLY)) {
            parser.parseBody(builder);
        }
        
        return completeStatement(builder, marker, nodeType);
    }

    public static void parseCompleteMethodSignature(PsiBuilder builder,
                                                       GoParser parser) {
        parseSignature(builder, parser);

        if (builder.getTokenType() == pLPAREN) {

            PsiBuilder.Marker result = builder.mark();
            parseSignature(builder, parser);
            result.done(FUNCTION_RESULT);

            return;
        }

        PsiBuilder.Marker result = builder.mark();
        if (parser.parseType(builder) == null) {
            result.drop();
            return;
        }

        result.done(FUNCTION_PARAMETER);
        result = result.precede();

        result.done(FUNCTION_PARAMETER_LIST);
        result = result.precede();

        result.done(FUNCTION_RESULT);
    }

    /**
     * Receiver     := "(" [ identifier ] [ "*" ] BaseTypeName ")" .
     * BaseTypeName := identifier .
     *
     * @param builder PsiBuilder
     * @param parser GoParser
     */
    private static void parseReceiverDeclaration(PsiBuilder builder,
                                                 GoParser parser) {

        ParserUtils.getToken(builder, pLPAREN, "open.parenthesis.expected");

        PsiBuilder.Marker receiverDeclarationMarker = builder.mark();

        if (ParserUtils.lookAhead(builder, mIDENT, mIDENT) ||
            ParserUtils.lookAhead(builder, mIDENT, oMUL)) {
            ParserUtils.eatElement(builder, LITERAL_IDENTIFIER);
        }

        parser.parseType(builder);
        receiverDeclarationMarker.done(METHOD_RECEIVER);
        ParserUtils.getToken(builder, pRPAREN, GoBundle.message("error.closing.para.expected"));
    }

    public static void parseSignature(PsiBuilder builder, GoParser parser) {

        ParserUtils.getToken(builder, pLPAREN, "open.parenthesis.expected");

        if (ParserUtils.getToken(builder, pRPAREN))
            return;

        PsiBuilder.Marker signature = builder.mark();

        while (!builder.eof() && !ParserUtils.lookAhead(builder, pRPAREN)) {
            int pos = builder.getCurrentOffset();

            parseParameterDeclaration(builder, parser);

            if (!builder.eof() && !ParserUtils.lookAhead(builder, pRPAREN)) {
                ParserUtils.getToken(builder, oCOMMA,
                                     GoBundle.message("error.comma.expected"));
            }

            if (pos == builder.getCurrentOffset()) {
                ParserUtils.wrapError(builder, "unexpected.char");
            }
        }

        signature.done(FUNCTION_PARAMETER_LIST);
        ParserUtils.getToken(builder, pRPAREN, "close.parenthesis.expected");
    }

    private static void parseParameterDeclaration(PsiBuilder builder,
                                                  GoParser parser) {
        PsiBuilder.Marker mark = builder.mark();

        IElementType parameterType = FUNCTION_PARAMETER;

        int identifiers = parser.parseIdentifierList(builder, false);

        if (ParserUtils.lookAhead(builder, pRPAREN) ||
            ParserUtils.lookAhead(builder, oCOMMA) ||
            ParserUtils.lookAhead(builder, oDOT, mIDENT)) {
            mark.rollbackTo();
            mark = builder.mark();
            parser.parseType(builder);
            mark.done(parameterType);
            return;
        }

        if (ParserUtils.getToken(builder, oTRIPLE_DOT)) {
            parameterType = FUNCTION_PARAMETER_VARIADIC;
        }

        if (ParserUtils.lookAhead(builder, oDOT, mIDENT) && identifiers == 1) {
            mark.rollbackTo();
            mark = builder.mark();
        }

        parser.parseType(builder);
        mark.done(parameterType);
    }

    private static boolean tryParameterListAsAnonymousTypes(PsiBuilder builder,
                                                            GoParser parser) {
        PsiBuilder.Marker signature = builder.mark();

        int parameterCount = 0;
        boolean isVariadic = false;

        // first try to parse as a list of types
        while (!builder.eof()) {
            PsiBuilder.Marker argument = builder.mark();

            if (ParserUtils.getToken(builder, oTRIPLE_DOT)) {
                isVariadic = true;
            }

            if (parser.parseType(builder) != null) {
                argument.done(isVariadic
                                  ? FUNCTION_PARAMETER_VARIADIC
                                  : FUNCTION_PARAMETER);
                parameterCount++;
            } else {
                argument.drop();
            }

            if (!ParserUtils.getToken(builder, oCOMMA))
                break;
        }

        if (!ParserUtils.getToken(builder, pRPAREN)) {
            signature.rollbackTo();
            return false;
        }

        if (parameterCount > 0) {
            signature.done(FUNCTION_PARAMETER_LIST);
        } else {
            signature.drop();
        }

        return true;
    }
}
