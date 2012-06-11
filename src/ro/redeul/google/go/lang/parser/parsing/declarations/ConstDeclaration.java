package ro.redeul.google.go.lang.parser.parsing.declarations;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;
import static ro.redeul.google.go.lang.parser.GoParser.ParsingFlag.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 9:38:55 PM
 */
public class ConstDeclaration implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kCONST)) {
            ParserUtils.wrapError(builder, "const.keyword.expected");
            marker.drop();
            return null;
        }

        NestedDeclarationParser.parseNestedOrBasicDeclaration(
            builder, parser, new NestedDeclarationParser.DeclarationParser() {
            public void parse(PsiBuilder builder, GoParser parser) {
                parseConstSpecification(builder, parser);
            }
        });

        marker.done(CONST_DECLARATIONS);
        return CONST_DECLARATIONS;
    }

    private static boolean parseConstSpecification(PsiBuilder builder,
                                                   GoParser parser) {

        PsiBuilder.Marker initializer = builder.mark();

        if (parser.parseIdentifierList(builder, false) == 0) {
            initializer.drop();
            return false;
        }

        if (!ParserUtils.lookAhead(builder, oASSIGN)) {
            parser.parseType(builder);
        }

        if (ParserUtils.lookAhead(builder, oASSIGN)) {
            ParserUtils.advance(builder);

            boolean parseIota = parser.resetFlag(ParseIota, true);
            do {
                parser.parseExpression(builder);
            } while (ParserUtils.getToken(builder, oCOMMA));
            parser.resetFlag(ParseIota, parseIota);
        }

        if (builder.getTokenType() != oSEMI &&
            builder.getTokenType() != pRPAREN &&
            builder.getTokenType() != wsNLS) {
            builder.error("semicolon.or.newline.or.right.parenthesis.expected");
        }

        ParserUtils.getToken(builder, oSEMI);
        initializer.done(CONST_DECLARATION);

        return true;
    }
}
