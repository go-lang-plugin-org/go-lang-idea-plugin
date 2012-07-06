package ro.redeul.google.go.lang.parser.parsing.declarations;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 9:38:55 PM
 */
public class VarDeclaration extends ParserUtils implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if (!getToken(builder, kVAR)) {
            marker.rollbackTo();
            return null;
        }

        NestedDeclarationParser.parseNestedOrBasicDeclaration(
            builder, parser,
            new NestedDeclarationParser.DeclarationParser() {
                public boolean parse(PsiBuilder builder, GoParser parser) {
                    return parseVarSpecification(builder, parser);
                }
            });

        marker.done(VAR_DECLARATIONS);
        return VAR_DECLARATIONS;
    }

    static TokenSet localImportTokens = TokenSet.create(mIDENT, oDOT);

    private static boolean parseVarSpecification(PsiBuilder builder,
                                                 GoParser parser) {

        PsiBuilder.Marker varSpec = builder.mark();
        if (parser.parseIdentifierList(builder, false) != 0) {
            if (!ParserUtils.lookAhead(builder, GoTokenTypeSets.EOS)) {

                if (ParserUtils.getToken(builder, oASSIGN)) {
                    parser.parseExpressionList(builder);
                } else {
                    parser.parseType(builder);
                    if (ParserUtils.getToken(builder, oASSIGN)) {
                        parser.parseExpressionList(builder);
                    }
                }
            }

            varSpec.done(VAR_DECLARATION);
            return true;
        }

        varSpec.drop();
        builder.error("identifier.list.expected");
        return false;
    }
}
