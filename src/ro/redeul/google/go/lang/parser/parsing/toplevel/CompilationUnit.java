package ro.redeul.google.go.lang.parser.parsing.toplevel;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.toplevel.packaging.ImportDeclaration;
import ro.redeul.google.go.lang.parser.parsing.toplevel.packaging.PackageDeclaration;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 9:09:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompilationUnit implements GoElementTypes {

    public static void parse(PsiBuilder builder, GoParser parser) {

        PackageDeclaration.parse(builder);

        while (ParserUtils.lookAhead(builder, kIMPORT)) {
            ImportDeclaration.parse(builder, parser);
        }

        parser.parseTopLevelDeclarations(builder);
    }
}
