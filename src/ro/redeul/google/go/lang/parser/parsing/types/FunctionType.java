package ro.redeul.google.go.lang.parser.parsing.types;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 25, 2010
 * Time: 2:52:27 AM
 */
class FunctionType implements GoElementTypes {
    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!ParserUtils.lookAhead(builder, kFUNC))
            return null;

        PsiBuilder.Marker marker = builder.mark();

        ParserUtils.getToken(builder, kFUNC);

        parser.parseFunctionSignature(builder);

        marker.done(TYPE_FUNCTION);
        return TYPE_FUNCTION;
    }
}
