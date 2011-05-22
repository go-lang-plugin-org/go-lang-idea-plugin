package ro.redeul.google.go.lang.parser.parsing.expressions;

import com.intellij.lang.PsiBuilder;
import org.apache.velocity.runtime.directive.Parse;
import ro.redeul.google.go.lang.lexer.GoElementType;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

import javax.swing.text.html.parser.Parser;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 25, 2010
 * Time: 2:18:28 AM
 */
public class LiteralExpression implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker mark = builder.mark();

        PsiBuilder.Marker identifier = null;

        if ( ParserUtils.lookAhead(builder, mIDENT) ) {
            identifier = builder.mark();

            if ( parser.isPackageName(builder.getTokenText()) && ParserUtils.lookAhead(builder, mIDENT, oDOT) ) {
                ParserUtils.getToken(builder, mIDENT);
                ParserUtils.getToken(builder, oDOT);
            }
        }

        if ( ! ParserUtils.getToken(builder, GoTokenTypeSets.LITERALS, "literal.expected") ) {
            if  (identifier != null ) {
                identifier.drop();
            }

            mark.drop();
            return false;
        }

        if ( identifier != null ) {
            identifier.done(IDENTIFIER);
        }

        mark.done(LITERAL_EXPRESSION);

        return true;
    }    
}
