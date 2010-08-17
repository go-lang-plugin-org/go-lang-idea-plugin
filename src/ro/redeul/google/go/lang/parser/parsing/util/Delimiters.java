package ro.redeul.google.go.lang.parser.parsing.util;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 16, 2010
 * Time: 9:16:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Delimiters implements GoElementTypes {
    static TokenSet delimiters = TokenSet.create(oSEMI, wsNLS);

    public static void parse(PsiBuilder builder, GoParser parser) {
        ParserUtils.getToken(builder, delimiters);
    }
}
