package ro.redeul.google.go.lang.parser.parsing.declarations;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/20/11
 * Time: 5:11 PM
 */
public class NestedDeclarationParser implements GoElementTypes {

    public static final TokenSet DECLARATION_END = TokenSet.create(oSEMI, pRPAREN, wsNLS);

    public static void parseNestedOrBasicDeclaration(PsiBuilder builder, GoParser parser, DeclarationParser declarationParser) {

        if (ParserUtils.lookAhead(builder, pLPAREN)) {
            ParserUtils.advance(builder);

            do {
                declarationParser.parse(builder, parser);

                ParserUtils.skipComments(builder);

                if ( !builder.eof() && !DECLARATION_END.contains(builder.getTokenType() ) ) {
                    ParserUtils.wrapError(builder, "semicolon.or.newline.or.closed.parenthesis.expected");
                } else {
                    ParserUtils.getToken(builder, oSEMI);
                    ParserUtils.skipNLS(builder);
                }
            } while (!ParserUtils.lookAhead(builder, pRPAREN) && !builder.eof());

            ParserUtils.advance(builder);

        } else {
            declarationParser.parse(builder, parser);
        }
    }

    public interface DeclarationParser {
        void parse(PsiBuilder builder, GoParser parser);
    }
}
