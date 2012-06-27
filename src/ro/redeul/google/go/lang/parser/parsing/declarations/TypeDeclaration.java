package ro.redeul.google.go.lang.parser.parsing.declarations;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.types.Types;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 9:38:55 PM
 */
public class TypeDeclaration implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kTYPE)) {
            ParserUtils.wrapError(builder, "type.keyword.expected");
            marker.drop();
            return null;
        }

        NestedDeclarationParser.parseNestedOrBasicDeclaration(
            builder, parser,
            new NestedDeclarationParser.DeclarationParser() {
                public void parse(PsiBuilder builder, GoParser parser) {
                    parseTypeSpecification(builder, parser);
                }
            });

        marker.done(TYPE_DECLARATIONS);
        return TYPE_DECLARATIONS;
    }

    static TokenSet localImportTokens = TokenSet.create(mIDENT, oDOT);

    private static boolean parseTypeSpecification(PsiBuilder builder,
                                                  GoParser parser) {

        if (!ParserUtils.lookAhead(builder, mIDENT)){
            builder.error("error.identifier.expected");
            return false;
        }

        PsiBuilder.Marker typeStatement = builder.mark();
        ParserUtils.eatElement(builder, TYPE_NAME_DECLARATION);

        if (Types.parseTypeDeclaration(builder, parser) == null) {
            builder.error(GoBundle.message("error.type.expected"));
        }

        typeStatement.done(TYPE_DECLARATION);

        return true;
    }

}
