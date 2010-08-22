package ro.redeul.google.go.lang.parser.parsing.toplevel.packaging;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 9:11:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class PackageDeclaration implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker packageDeclaration = builder.mark();


        if (ParserUtils.getToken(builder, kPACKAGE, GoBundle.message("package.keyword.expected"))) {
            PsiBuilder.Marker marker = builder.mark();

            ParserUtils.skipNLS(builder);

            if (!ParserUtils.getToken(builder, mIDENT, GoBundle.message("identifier.expected"))) {
                marker.rollbackTo();
                builder.error(GoBundle.message("identifier.expected"));
            } else {
                marker.drop();
            }
        } else {
            ParserUtils.getToken(builder, mIDENT);
        }
        
        if (builder.getTokenType() != oSEMI && builder.getTokenType() != wsNLS) {
            builder.error("semicolon.or.newline.expected");
        }

        ParserUtils.getToken(builder, oSEMI);
        packageDeclaration.done(PACKAGE_DECLARATION);

        return true;
    }
}
