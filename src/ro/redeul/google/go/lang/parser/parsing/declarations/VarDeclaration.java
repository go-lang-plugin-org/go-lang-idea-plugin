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
class VarDeclaration extends ParserUtils implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker varDeclarations = builder.mark();

        if (!getToken(builder, kVAR)) {
            varDeclarations.rollbackTo();
            return null;
        }

        NestedDeclarationParser.parseNestedOrBasicDeclaration(
            builder, parser,
            new NestedDeclarationParser.DeclarationParser() {
                public boolean parse(PsiBuilder builder, GoParser parser) {
                    return parseVarSpecification(builder, parser);
                }
            });

        varDeclarations.done(VAR_DECLARATIONS);
        varDeclarations.setCustomEdgeTokenBinders(null, CommentBinders.TRAILING_COMMENTS);
        return VAR_DECLARATIONS;
    }

    static TokenSet localImportTokens = TokenSet.create(mIDENT, oDOT);

    private static boolean parseVarSpecification(PsiBuilder builder,
                                                 GoParser parser) {

        PsiBuilder.Marker declaration = builder.mark();
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

            declaration.done(VAR_DECLARATION);
            declaration.setCustomEdgeTokenBinders(null, CommentBinders.TRAILING_COMMENTS);
            return true;
        }

        declaration.drop();
        builder.error("identifier.list.expected");
        return false;
    }
}
