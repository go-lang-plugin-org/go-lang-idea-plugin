package ro.redeul.google.go.lang.parser.parsing.declarations;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 9:38:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConstDeclaration implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if ( ! ParserUtils.getToken(builder, kCONST) ) {
            ParserUtils.wrapError(builder, "const.keyword.expected");
            marker.drop();
            return false;
        }                

        ParserUtils.skipNLS(builder);

        if ( builder.getTokenType() == pLPAREN ) {
            ParserUtils.eatElement(builder, pLPAREN);

            do {
                ParserUtils.skipNLS(builder);
                if ( ! parseConstSpecification(builder, parser) ) {
                    if ( wsNLS != builder.getTokenType()) {
                        builder.advanceLexer();
                    }
                }
                ParserUtils.skipNLS(builder);
            } while ( ! builder.eof() && builder.getTokenType() != pRPAREN );

            ParserUtils.skipNLS(builder);
            ParserUtils.eatElement(builder, pRPAREN);

        } else {
            parseConstSpecification(builder, parser);
        }

        marker.done(CONST_DECLARATIONS);
        return true;
    }

    static TokenSet localImportTokens = TokenSet.create(mIDENT, oDOT);

    private static boolean parseConstSpecification(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker initializer = builder.mark();

        if ( parser.parseIdentifierList(builder) == 0 ) {
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
        initializer.done(CONST_SPEC);
        
        return true;
    }
}
