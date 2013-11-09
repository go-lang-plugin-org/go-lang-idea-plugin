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
class ConstDeclaration implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!ParserUtils.lookAhead(builder, kCONST))
            return null;

        PsiBuilder.Marker marker = builder.mark();

        ParserUtils.getToken(builder, kCONST);

        NestedDeclarationParser.parseNestedOrBasicDeclaration(
            builder, parser, new NestedDeclarationParser.DeclarationParser() {
            public boolean parse(PsiBuilder builder, GoParser parser) {
                return parseConstSpecification(builder, parser);
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

        if (ParserUtils.getToken(builder, oASSIGN)) {
            boolean parseIota = parser.resetFlag(ParseIota, true);
            parser.parseExpressionList(builder);
            parser.resetFlag(ParseIota, parseIota);
        }

        initializer.done(CONST_DECLARATION);

        return true;
    }
}
