package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 7:44:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlockStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if ( ! ParserUtils.lookAhead(builder, pLCURCLY) )
            return null;

        PsiBuilder.Marker block = builder.mark();

        ParserUtils.getToken(builder, pLCURCLY);
        while ( !builder.eof() && builder.getTokenType() != pRCURLY ) {

            IElementType statementType = parser.parseStatement(builder);
            if ( statementType == null || statementType == EMPTY_STATEMENT) {

                ParserUtils.waitNext(builder, TokenSet.create(wsNLS, oSEMI, pRCURLY));
//                marker.error(GoBundle.message("error.statement.expected"));
            }

            ParserUtils.endStatement(builder);
        }

        ParserUtils.getToken(builder, pRCURLY, "right.curly.expected");
        block.done(BLOCK_STATEMENT);

        return BLOCK_STATEMENT;
    }
}
