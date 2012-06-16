package ro.redeul.google.go.lang.parser.parsing.types;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

public class InterfaceType implements GoElementTypes {
    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!ParserUtils.lookAhead(builder, kINTERFACE))
            return null;

        PsiBuilder.Marker type = builder.mark();
        ParserUtils.getToken(builder, kINTERFACE);

        ParserUtils.skipNLS(builder);
        parseInterfaceBlock(builder, parser);

        type.done(TYPE_INTERFACE);
        return TYPE_INTERFACE;
    }

    private static void parseInterfaceBlock(PsiBuilder builder,
                                            GoParser parser) {
        ParserUtils.getToken(builder, pLCURCLY, "left.curly.expected");

        while (!builder.eof()) {
            ParserUtils.skipNLS(builder);

            if (builder.getTokenType() == pRCURLY)
                break;

            parseMethodSpec(builder, parser);
        }
        ParserUtils.skipNLS(builder);

        ParserUtils.getToken(builder, pRCURLY, "right.curly.expected");
    }

    private static void parseMethodSpec(PsiBuilder builder, GoParser parser) {
        ParserUtils.skipNLS(builder);

        PsiBuilder.Marker methodSpec = builder.mark();

        IElementType type = METHOD_DECLARATION;
        if (ParserUtils.lookAhead(builder, mIDENT, pLPAREN)) {
            ParserUtils.getToken(builder, mIDENT,
                                 GoBundle.message("error.method.name.expected"));
            parser.parseFunctionSignature(builder);
        } else {
            parser.parseTypeName(builder);

        }

//        if (builder.getTokenType() == pLPAREN) {
//            parser.parseMethodSignature(builder);
//        } else if (builder.getTokenType() != wsNLS) {
//            parser.parseType(builder);
//        }
//
//        if (builder.getTokenType() != wsNLS && builder.getTokenType() != oSEMI) {
//            builder.error("newline.or.semicolon.expected");
//        } else if (builder.getTokenType() == oSEMI) {
//            ParserUtils.advance(builder);
//        }
//
        methodSpec.done(type);
    }
}
