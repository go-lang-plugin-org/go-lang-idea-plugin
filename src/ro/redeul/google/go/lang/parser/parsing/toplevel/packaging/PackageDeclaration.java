package ro.redeul.google.go.lang.parser.parsing.toplevel.packaging;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 9:11:05 PM
 */
public class PackageDeclaration implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker packageDeclaration = builder.mark();

        if (ParserUtils.getToken(builder, kPACKAGE, GoBundle.message("error.package.keyword.expected"))) {
            ParserUtils.getToken(builder, mIDENT, GoBundle.message("identifier.expected"));
        }

        packageDeclaration.done(PACKAGE_DECLARATION);

        return true;
    }
}
