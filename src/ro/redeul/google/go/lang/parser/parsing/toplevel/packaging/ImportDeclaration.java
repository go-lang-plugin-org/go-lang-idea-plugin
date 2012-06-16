package ro.redeul.google.go.lang.parser.parsing.toplevel.packaging;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.declarations.NestedDeclarationParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.cleanupImportPath;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findDefaultPackageName;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 9:21:12 PM
 */
public class ImportDeclaration implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!ParserUtils.lookAhead(builder, kIMPORT))
            return null;

        PsiBuilder.Marker marker = builder.mark();

        ParserUtils.getToken(builder, kIMPORT);

        NestedDeclarationParser.parseNestedOrBasicDeclaration(
            builder, parser, new NestedDeclarationParser.DeclarationParser() {
            public void parse(PsiBuilder builder, GoParser parser) {
                parseImportStatement(builder, parser);
            }
        });

        marker.done(IMPORT_DECLARATIONS);
        return IMPORT_DECLARATIONS;
    }

    static TokenSet localImportTokens = TokenSet.create(mIDENT, oDOT, litSTRING);

    private static boolean parseImportStatement(PsiBuilder builder, GoParser parser) {

        if (!ParserUtils.lookAhead(builder, localImportTokens)) {
            return false;
        }

        PsiBuilder.Marker importStatement = builder.mark();

        String localPackageName = null;
        if (ParserUtils.lookAhead(builder, mIDENT) ) {
            localPackageName = builder.getTokenText();
            ParserUtils.eatElement(builder, PACKAGE_REFERENCE);
        } else if (ParserUtils.lookAhead(builder, oDOT)) {
            ParserUtils.eatElement(builder, PACKAGE_REFERENCE);
        }

        String importPath = builder.getTokenText();
        if (ParserUtils.getToken(builder, litSTRING, GoBundle.message("error.import.path.expected"))) {
            if ( localPackageName == null) {
                localPackageName = findDefaultPackageName(cleanupImportPath(importPath));
            }
        }

        if (localPackageName != null) {
            parser.setKnownPackage(localPackageName);
        }

        importStatement.done(IMPORT_DECLARATION);
        return true;
    }
}