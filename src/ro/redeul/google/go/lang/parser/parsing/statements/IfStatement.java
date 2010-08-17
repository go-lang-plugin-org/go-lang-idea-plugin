package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 8:01:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class IfStatement implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {
    
        PsiBuilder.Marker marker = builder.mark();

        if (!ParserUtils.getToken(builder, kIF)) {
            marker.rollbackTo();
            return false;
        }

        parser.tryParseSimpleStmt(builder, true);

        if (builder.getTokenType() != pLCURCLY) {
            if ( ParserUtils.getToken(builder, oSEMI) ) {
                ParserUtils.skipNLS(builder);
            }
            
            parser.parseExpression(builder, true);
        }

        parser.parseBody(builder);

        ParserUtils.skipNLS(builder);
        if (builder.getTokenType() == kELSE) {
            ParserUtils.getToken(builder, kELSE);
            ParserUtils.skipNLS(builder);
            Statements.parse(builder, parser, false);
        }
        
        marker.done(IF_STATEMENT);
        return true;

    }
}
