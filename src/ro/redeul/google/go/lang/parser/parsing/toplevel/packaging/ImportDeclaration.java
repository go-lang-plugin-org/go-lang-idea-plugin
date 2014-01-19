package ro.redeul.google.go.lang.parser.parsing.toplevel.packaging;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.declarations.Declaration;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.completeNodeWithDocs;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findDefaultPackageName;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 9:21:12 PM
 */
public class ImportDeclaration implements GoElementTypes {

    public static void parse(PsiBuilder builder, GoParser parser) {

        if (!ParserUtils.lookAhead(builder, kIMPORT))
            return;

        Declaration.parseDeclaration(builder, parser, kIMPORT, IMPORT_DECLARATIONS, ImportSpecParser);
    }

    private static final Declaration.SpecParser ImportSpecParser = new Declaration.SpecParser() {

        private final TokenSet localImportTokens = TokenSet.create(mIDENT, oDOT, litSTRING);

        @Override
        public boolean parse(PsiBuilder builder, GoParser parser) {

            if (!ParserUtils.lookAhead(builder, localImportTokens)) {
                return false;
            }

            PsiBuilder.Marker importStatement = builder.mark();

            String localPackageName = null;
            if (ParserUtils.lookAhead(builder, mIDENT)) {
                localPackageName = builder.getTokenText();
                ParserUtils.eatElement(builder, PACKAGE_REFERENCE);
            } else if (ParserUtils.lookAhead(builder, oDOT)) {
                ParserUtils.eatElement(builder, PACKAGE_REFERENCE);
            }

            PsiBuilder.Marker importPathMarker = builder.mark();
            String importPath = builder.getTokenText();
            if (!ParserUtils.getToken(builder, litSTRING, GoBundle.message("error.import.path.expected"))) {
                importPathMarker.drop();
            } else {
                importPathMarker.done(LITERAL_STRING);
                if (localPackageName == null) {
                    localPackageName = findDefaultPackageName(GoPsiUtils.getStringLiteralValue(importPath));
                }
            }

            if (localPackageName != null) {
                parser.setKnownPackage(localPackageName);
            }

            completeNodeWithDocs(builder, importStatement, IMPORT_DECLARATION, true, true, true);
            return true;
        }
    };
}