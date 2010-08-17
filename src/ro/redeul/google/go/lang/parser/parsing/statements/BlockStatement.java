package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
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

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker block = builder.mark();
        if ( ! ParserUtils.getToken(builder, pLCURCLY) ) {
            block.rollbackTo();
            return false;
        }
        do {
            ParserUtils.skipNLS(builder);
            if ( ! parser.parseStatement(builder) ) {
                builder.advanceLexer();
            }
            ParserUtils.getToken(builder, oSEMI);
            ParserUtils.skipNLS(builder);
        } while ( ! builder.eof() && builder.getTokenType() != pRCURLY );

        ParserUtils.getToken(builder, pRCURLY, "right.curly.expected");        
        block.done(BLOCK_STATEMENT);

        return true;

    }
    
}
