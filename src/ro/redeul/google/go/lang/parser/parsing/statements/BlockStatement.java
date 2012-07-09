package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 7:44:49 PM
 */
public class BlockStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if ( ! ParserUtils.lookAhead(builder, pLCURLY) )
            return null;

        PsiBuilder.Marker block = builder.mark();

        ParserUtils.getToken(builder, pLCURLY);
        while ( !builder.eof() && builder.getTokenType() != pRCURLY ) {

            IElementType statementType = parser.parseStatement(builder);
            if ( statementType == null || statementType == EMPTY_STATEMENT) {

                ParserUtils.waitNext(builder, TokenSet.create(oSEMI, oSEMI_SYNTHETIC, pRCURLY));
//                marker.error(GoBundle.message("error.statement.expected"));
            }

            ParserUtils.endStatement(builder);
        }

        ParserUtils.getToken(builder, pRCURLY, "right.curly.expected");
        block.done(BLOCK_STATEMENT);

        return BLOCK_STATEMENT;
    }
}
