package ro.redeul.google.go.lang.parser.parsing.types;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 2:52:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class FunctionType implements GoElementTypes {
    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if ( ! ParserUtils.getToken(builder, kFUNC, "func.keyword.expected") ) {
            marker.drop();            
            return false;
        }

        ParserUtils.skipNLS(builder);

        parser.parseFunctionSignature(builder);

        marker.done(TYPE_FUNCTION);
        return true;
    }
}
