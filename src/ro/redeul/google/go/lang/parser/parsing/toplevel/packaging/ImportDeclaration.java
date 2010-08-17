package ro.redeul.google.go.lang.parser.parsing.toplevel.packaging;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.Delimiters;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 9:21:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImportDeclaration implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        if ( ! ParserUtils.getToken(builder, kIMPORT) ) {
            marker.rollbackTo();
            return false;
        }

        ParserUtils.skipNLS(builder);
        
        if ( builder.getTokenType() == pLPAREN ) {
            ParserUtils.getToken(builder, pLPAREN);

            ParserUtils.skipNLS(builder);

            do {                
                long position = builder.getCurrentOffset();
                parseImportStatement(builder, parser);
                Delimiters.parse(builder, parser);
                if ( position == builder.getCurrentOffset() ) {
                    builder.error("unexpected.token");
                    break;
                }
                ParserUtils.skipNLS(builder);
            } while ( ! builder.eof() && pRPAREN != builder.getTokenType() );

            ParserUtils.skipNLS(builder);
            ParserUtils.getToken(builder, pRPAREN, "closed.parenthesis.expected");
            
        } else {
            parseImportStatement(builder, parser);
        }
        
        marker.done(IMPORT_DECLARATION);
        return true;
    }

    static TokenSet localImportTokens = TokenSet.create(mIDENT, oDOT);

    private static boolean parseImportStatement(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker importStatement = builder.mark();

        if ( localImportTokens.contains(builder.getTokenType())) {
            ParserUtils.eatElement(builder, PACKAGE_REFERENCE);
        }

        ParserUtils.skipNLS(builder);

        boolean parsed = false;
        PsiBuilder.Marker importPath = builder.mark();
        if ( ! ParserUtils.getToken(builder, litSTRING) ) {
            importPath.rollbackTo();
            builder.error("import.path.expected");
        } else {
            parsed = true;
            importPath.drop();
        }

        if ( ! builder.eof() && ! TokenSet.create(oSEMI, wsNLS, pRPAREN).contains(builder.getTokenType()) ) {
            builder.error("semicolon.or.newline.right.parenthesis.expected");
        }

        ParserUtils.getToken(builder, oSEMI);
        
        importStatement.done(IMPORT_SPEC);
        return parsed;
    }
}