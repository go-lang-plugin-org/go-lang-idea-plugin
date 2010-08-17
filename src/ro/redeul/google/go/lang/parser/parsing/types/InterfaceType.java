package ro.redeul.google.go.lang.parser.parsing.types;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 2:52:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class InterfaceType implements GoElementTypes {
    public static boolean parse(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker type = builder.mark();

        if ( ! ParserUtils.getToken(builder, kINTERFACE) ) {
            type.rollbackTo();
            return false;
        }

        ParserUtils.skipNLS(builder);
        parseInterfaceBlock(builder, parser);

        type.done(TYPE_INTERFACE);
        return true;
    }

    private static void parseInterfaceBlock(PsiBuilder builder, GoParser parser) {
        ParserUtils.getToken(builder, pLCURCLY, "left.curly.expected");

        while ( ! builder.eof()) {
            ParserUtils.skipNLS(builder);

            if ( builder.getTokenType() == pRCURLY)
                break;

            parseMethodSpec(builder, parser);
        }

        ParserUtils.getToken(builder, pRCURLY, "right.curly.expected");
    }

    private static void parseMethodSpec(PsiBuilder builder, GoParser parser) {
        ParserUtils.skipNLS(builder);

        PsiBuilder.Marker methodSpec = builder.mark();

        PsiBuilder.Marker methodName = builder.mark();
        if ( ParserUtils.getToken(builder, mIDENT) ) {
            methodName.done(IDENTIFIER);
        } else {
            methodName.error("identifier.expected");
        }

        if ( builder.getTokenType() == wsNLS ) {
            methodSpec.done(INTERFACE_REFERENCE);
            return;
        }

        parser.parseMethodSignature(builder);

        if ( builder.getTokenType() == pLPAREN ) {
            parser.parseMethodSignature(builder);
        } else if ( builder.getTokenType() != wsNLS ) {
            parser.parseType(builder);
        }

        if ( builder.getTokenType() != wsNLS && builder.getTokenType() != oSEMI ) {
            builder.error("newline.or.semicolon.expected");
        } else if ( builder.getTokenType() == oSEMI ) {
            ParserUtils.advance(builder);
        }

        methodSpec.done(METHOD_DECLARATION);        
    }
}
