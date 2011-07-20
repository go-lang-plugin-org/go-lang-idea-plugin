package ro.redeul.google.go.lang.parser.parsing.declarations;

import com.intellij.application.options.colors.ColorAndFontSettingsListener;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.lexer.GoElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 9:38:55 PM
 */
public class ConstDeclaration implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if ( ! ParserUtils.getToken(builder, kCONST) ) {
            ParserUtils.wrapError(builder, "const.keyword.expected");
            marker.drop();
            return false;
        }                

        NestedDeclarationParser.parseNestedOrBasicDeclaration(builder, parser, new NestedDeclarationParser.DeclarationParser() {
            public void parse(PsiBuilder builder, GoParser parser) {
                parseConstSpecification(builder, parser);
            }
        });

        marker.done(CONST_DECLARATIONS);
        return true;
    }

    private static boolean parseConstSpecification(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker initializer = builder.mark();

        if ( parser.parseIdentifierList(builder, false) == 0 ) {
            initializer.drop();
            return false;
        }

        if ( ! ParserUtils.lookAhead(builder, oASSIGN))  {
            parser.parseType(builder);
        }

        if ( ParserUtils.lookAhead(builder, oASSIGN) ) {
            ParserUtils.advance(builder);

            PsiBuilder.Marker valuesList = builder.mark();
            do {
                parser.parseExpression(builder, false);
            } while (ParserUtils.getToken(builder, oCOMMA));
            valuesList.done(EXPRESSION_LIST);
        }

        if ( builder.getTokenType() != oSEMI && builder.getTokenType() != pRPAREN && builder.getTokenType() != wsNLS ) {
            builder.error("semicolon.or.newline.or.right.parenthesis.expected");
        }
        
        ParserUtils.getToken(builder, oSEMI);
        initializer.done(CONST_DECLARATION);
        
        return true;
    }
}
