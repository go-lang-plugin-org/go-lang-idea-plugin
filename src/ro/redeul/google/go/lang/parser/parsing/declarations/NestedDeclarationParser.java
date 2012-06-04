package ro.redeul.google.go.lang.parser.parsing.declarations;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.advance;
import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.getToken;
import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.lookAhead;
import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.skipComments;
import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.skipNLS;
import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.wrapError;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: 7/20/11
 */
public class NestedDeclarationParser implements GoElementTypes {

    public static final TokenSet DECLARATION_END = TokenSet.create(oSEMI,
                                                                   pRPAREN,
                                                                   wsNLS);

    public static void parseNestedOrBasicDeclaration(PsiBuilder builder,
                                                     GoParser parser,
                                                     DeclarationParser declarationParser) {

        if (lookAhead(builder, pLPAREN)) {
            advance(builder);

            do {
                skipNLS(builder);
                declarationParser.parse(builder, parser);

                skipComments(builder);

                if (!builder.eof() &&
                    !DECLARATION_END.contains(builder.getTokenType())) {

                    wrapError(builder,
                              "semicolon.or.newline.or.closed.parenthesis.expected");
                } else {
                    getToken(builder, oSEMI);
                    skipNLS(builder);
                }
            } while (!lookAhead(builder, pRPAREN) && !builder.eof());

            advance(builder);

        } else {
            declarationParser.parse(builder, parser);
        }
    }

    public interface DeclarationParser {
        void parse(PsiBuilder builder, GoParser parser);
    }
}
