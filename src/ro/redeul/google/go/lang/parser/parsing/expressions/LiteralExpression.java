package ro.redeul.google.go.lang.parser.parsing.expressions;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 2:18:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class LiteralExpression implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker mark = builder.mark();
        if ( ! ParserUtils.getToken(builder, GoTokenTypeSets.LITERALS, "literal.expected") ) {
            mark.drop();
            return false;
        }

        mark.done(LITERAL);

        return true;
    }    
}
