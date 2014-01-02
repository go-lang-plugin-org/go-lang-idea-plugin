package ro.redeul.google.go.lang.parser.parsing.types;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.CommentBinders;

class InterfaceType implements GoElementTypes {
    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!ParserUtils.lookAhead(builder, kINTERFACE))
            return null;

        PsiBuilder.Marker type = builder.mark();
        ParserUtils.getToken(builder, kINTERFACE);

        parseInterfaceBlock(builder, parser);

        type.done(TYPE_INTERFACE);
        return TYPE_INTERFACE;
    }

    private static void parseInterfaceBlock(PsiBuilder builder,
                                            GoParser parser) {
        ParserUtils.getToken(builder, pLCURLY, "left.curly.expected");

        while (!builder.eof() && ! ParserUtils.lookAhead(builder, pRCURLY) ) {

            if ( ! parseMethodSpec(builder, parser) )
                break;

            ParserUtils.endStatement(builder);
        }

        ParserUtils.getToken(builder, pRCURLY, "right.curly.expected");
    }

    private static boolean parseMethodSpec(PsiBuilder builder, GoParser parser) {

        if (ParserUtils.lookAhead(builder, mIDENT, pLPAREN)) {
            PsiBuilder.Marker methodSpec = builder.mark();
            ParserUtils.eatElement(builder, LITERAL_IDENTIFIER);
            parser.parseFunctionSignature(builder);
            methodSpec.done(FUNCTION_DECLARATION);
            methodSpec.setCustomEdgeTokenBinders(null, CommentBinders.TRAILING_COMMENTS);
            return true;
        }

        return ParserUtils.lookAhead(builder, mIDENT) && parser.parseTypeName(builder);
    }
}
