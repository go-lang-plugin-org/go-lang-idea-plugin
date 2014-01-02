package ro.redeul.google.go.lang.parser.parsing.declarations;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.*;
import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.getToken;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: 7/20/11
 */
public class NestedDeclarationParser implements GoElementTypes {

    public static void parseNestedOrBasicDeclaration(PsiBuilder builder,
                                                     GoParser parser,
                                                     DeclarationParser declarationParser) {

        if ( !ParserUtils.getToken(builder, pLPAREN)) {
            declarationParser.parse(builder, parser, false);
            return;
        }

        while (!builder.eof() && !lookAhead(builder, pRPAREN)) {
            if ( ! declarationParser.parse(builder, parser, true) ) {
                break;
            }
            skipComments(builder);
        }

        getToken(builder, pRPAREN, GoBundle.message("error.closing.para.expected"));
    }

    public interface DeclarationParser {
        boolean parse(PsiBuilder builder, GoParser parser, boolean shouldComplete);
    }
}
