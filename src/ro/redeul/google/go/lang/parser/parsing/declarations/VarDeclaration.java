package ro.redeul.google.go.lang.parser.parsing.declarations;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 9:38:55 PM
 */
public class VarDeclaration implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kVAR)) {
            marker.rollbackTo();
            return false;
        }

        NestedDeclarationParser.parseNestedOrBasicDeclaration(builder, parser, new NestedDeclarationParser.DeclarationParser() {
            public void parse(PsiBuilder builder, GoParser parser) {
                parseVarSpecification(builder, parser);
            }
        });

        marker.done(VAR_DECLARATIONS);
        return true;
    }

    static TokenSet localImportTokens = TokenSet.create(mIDENT, oDOT);

    private static void parseVarSpecification(PsiBuilder builder, GoParser parser) {

        ParserUtils.skipNLS(builder);
        PsiBuilder.Marker varStatementSpecification = builder.mark();

        if (parser.parseIdentifierList(builder, false) == 0) {
            builder.error("identifier.list.expected");
        }

        boolean hasType = false;
        ParserUtils.skipNLS(builder);
        if (builder.getTokenType() != oASSIGN) {
            hasType = true;
            parser.parseType(builder);
        }

        if (!hasType || (builder.getTokenType() != oSEMI && builder.getTokenType() != wsNLS)) {
            ParserUtils.skipNLS(builder);
            if (ParserUtils.getToken(builder, oASSIGN)) {
                ParserUtils.skipNLS(builder);
                parser.parseExpressionList(builder, false);
            } else {
                if (!hasType) {
                    builder.error("assign.operator.expected");
                }
            }
        }

        ParserUtils.waitNext(builder, TokenSet.create(oSEMI, wsNLS, pRPAREN, pRCURLY), "semicolon.or.newline.right.parenthesis.expected");
        ParserUtils.getToken(builder, oSEMI);
        varStatementSpecification.done(VAR_DECLARATION);
    }
}
