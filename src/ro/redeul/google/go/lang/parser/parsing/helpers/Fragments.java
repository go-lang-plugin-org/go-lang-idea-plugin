package ro.redeul.google.go.lang.parser.parsing.helpers;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 25, 2010
 * Time: 6:17:19 PM
 */
public class Fragments implements GoElementTypes {

    public static int parseIdentifierList(PsiBuilder builder) {
        return parseIdentifierList(builder, true);
    }

    public static int parseIdentifierList(PsiBuilder builder, boolean markList) {

        int length = 0;

        PsiBuilder.Marker list = null;
        if ( markList ) {
            list = builder.mark();
        }

        if ( ! (builder.getTokenType() == mIDENT) ) {
            if (markList) {
                list.rollbackTo();
            }
            return length;
        }

        while ( ParserUtils.lookAhead(builder, mIDENT) ) {
            ParserUtils.eatElement(builder, GoElementTypes.LITERAL_IDENTIFIER);

            length++;
            if (!ParserUtils.lookAhead(builder, oCOMMA, mIDENT)) {
                break;
            }

            getToken(builder, oCOMMA);
        }

        if (markList ) {
            list.done(IDENTIFIERS);
        }

        return length;
    }


    public static IElementType parseBlock(PsiBuilder builder, GoParser parser,
                                          boolean asStatement) {

        PsiBuilder.Marker block = builder.mark();

        if (!getToken(builder, pLCURLY)) {
            block.drop();
            return null;
        }

        while (!builder.eof() && builder.getTokenType() != pRCURLY) {
            IElementType statementType = parser.parseStatement(builder);

            if (statementType == null || statementType == EMPTY_STATEMENT) {
                waitNext(builder, TokenSet.create(oSEMI, oSEMI_SYNTHETIC, pRCURLY));
                getToken(builder, GoTokenTypeSets.EOS);
            }
        }

        if (!getToken(builder, pRCURLY, GoBundle.message("error.closing.curly.expected"))) {
            block.drop();
            return null;
        }

        if (asStatement)
            completeStatement(builder, block, BLOCK_STATEMENT);
        else
            block.done(BLOCK_STATEMENT);

        return BLOCK_STATEMENT;
    }
}
