package ro.redeul.google.go.lang.parser.parsing.expressions;

import com.intellij.lang.PsiBuilder;
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
public class FunctionLiteralExpression implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker mark = builder.mark();

        if ( builder.getTokenType() != kFUNC ) {
            mark.drop();
            return false;
        }

        parser.parseType(builder);

        ParserUtils.skipNLS(builder);
        parser.parseBody(builder);

        mark.done(LITERAL_FUNCTION);

        return true;
    }
}
