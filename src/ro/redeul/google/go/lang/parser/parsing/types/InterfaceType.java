package ro.redeul.google.go.lang.parser.parsing.types;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.CommentBinders;
import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.completeStatement;

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
        }

        ParserUtils.getToken(builder, pRCURLY, "right.curly.expected");
    }

    private static boolean parseMethodSpec(PsiBuilder builder, GoParser parser) {

        if (ParserUtils.lookAhead(builder, mIDENT, pLPAREN)) {
            PsiBuilder.Marker methodSpec = builder.mark();
            ParserUtils.eatElement(builder, LITERAL_IDENTIFIER);
            parser.parseFunctionSignature(builder);
            completeStatement(builder, methodSpec, FUNCTION_DECLARATION);
            return true;
        }

        if (ParserUtils.lookAhead(builder, mIDENT)) {
            try {
                parser.setFlag(GoParser.ParsingFlag.ShouldCompleteStatement);
                return parser.parseTypeName(builder);
            } finally {
                parser.unsetFlag(GoParser.ParsingFlag.ShouldCompleteStatement);
            }
        }
        return false;
    }
}
