package ro.redeul.google.go.lang.parser.parsing.declarations;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;
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

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if ( ! ParserUtils.getToken(builder, kTYPE) ) {
            ParserUtils.wrapError(builder, "type.keyword.expected");
            marker.drop();
            return false;
        }                

        if ( ParserUtils.lookAhead(builder, pLPAREN) ) {
            ParserUtils.advance(builder);

            do {
                parseTypeSpecification(builder, parser);

                if ( builder.getTokenType() != oSEMI  &&
                     builder.getTokenType() != pRPAREN  &&
                     builder.getTokenType() != wsNLS )
                {
                    builder.error("semicolon.or.newline.or.closed.parenthesis.expected");
                }
                else
                {
                    ParserUtils.getToken(builder, oSEMI);
                    ParserUtils.skipNLS(builder);
                }
            } while ( ! ParserUtils.lookAhead(builder, pRPAREN) && ! builder.eof() );

            ParserUtils.advance(builder);

        } else {
            parseTypeSpecification(builder, parser);
        }

        marker.done(TYPE_DECLARATIONS);
        return true;
    }

    static TokenSet localImportTokens = TokenSet.create(mIDENT, oDOT);

    private static boolean parseTypeSpecification(PsiBuilder builder, GoParser parser) {

        ParserUtils.skipNLS(builder);        
        PsiBuilder.Marker typeStatement = builder.mark();

//        ParserUtils.getToken(builder, mIDENT, "identifier.expected");

        if ( ParserUtils.lookAhead(builder, mIDENT) ) {
            ParserUtils.eatElement(builder, TYPE_NAME_DECLARATION);
        } else {
            ParserUtils.wrapError(builder, "identifier.expected");
        }

        if ( ! Types.parseTypeDeclaration(builder, parser) ) {
            builder.error("type.expected");
        }

        typeStatement.done(TYPE_DECLARATION);

        return true;
    }

}
