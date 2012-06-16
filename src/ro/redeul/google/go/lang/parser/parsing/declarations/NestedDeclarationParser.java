package ro.redeul.google.go.lang.parser.parsing.declarations;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;
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

        if ( !ParserUtils.getToken(builder, pLPAREN)) {
            declarationParser.parse(builder, parser);

            ParserUtils.getToken(builder, DECLARATION_END,
                                 GoBundle.message("semicolon.or.newline.or.closed.parenthesis.expected"));
            return;
        }

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
    }

    public interface DeclarationParser {
        void parse(PsiBuilder builder, GoParser parser);
    }
}
