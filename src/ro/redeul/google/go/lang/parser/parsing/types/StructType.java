package ro.redeul.google.go.lang.parser.parsing.types;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;
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
public class StructType implements GoElementTypes {
    public static boolean parse(PsiBuilder builder, GoParser parser) {
        PsiBuilder.Marker marker = builder.mark();

        if ( ! ParserUtils.getToken(builder, kSTRUCT)) {
            marker.rollbackTo();
            return false;
        }

        ParserUtils.getToken(builder, pLCURCLY, "left.curly.expected");

        do {
            parseFieldDeclaration(builder, parser);

            if ( builder.getTokenType() != oSEMI  &&
                 builder.getTokenType() != pRCURLY &&
                 builder.getTokenType() != wsNLS )
            {
                ParserUtils.wrapError(builder, "semicolon.or.newline.or.closed.curly.expected");
            }
            else
            {
                ParserUtils.getToken(builder, oSEMI);
                ParserUtils.skipNLS(builder);
            }
        } while ( ! builder.eof() && builder.getTokenType() != pRCURLY);

        ParserUtils.getToken(builder, pRCURLY);
        marker.done(TYPE_STRUCT);

        return true;

    }

    private static boolean parseFieldDeclaration(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker fieldDeclaration = builder.mark();

        if ( ParserUtils.lookAhead(builder, mIDENT, oDOT) ||
             ParserUtils.lookAhead(builder, oMUL, mIDENT) ||
             ParserUtils.lookAhead(builder, mIDENT, litSTRING) ||
             ParserUtils.lookAhead(builder, mIDENT, oSEMI) ||
             ParserUtils.lookAhead(builder, mIDENT, pRCURLY)
            ) {
            parseAnonymousField(builder, parser);
        } else if ( builder.getTokenType() == mIDENT )  {

            parser.parseIdentifierList(builder);

            parser.parseType(builder);
        }

        if ( builder.getTokenType() == litSTRING ) {
            ParserUtils.eatElement(builder, IDENTIFIER);
        }
        
        fieldDeclaration.done(TYPE_STRUCT_FIELD);

        return true;
    }

    private static boolean parseAnonymousField(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker anonymousType = builder.mark();

        if ( builder.getTokenType() == oMUL ) {
            ParserUtils.eatElement(builder, TYPE_STRUCT_FIELD_ADDRESS);
        }

        if ( ParserUtils.lookAhead(builder, mIDENT, oDOT) )  {
            PsiBuilder.Marker packageName = builder.mark();
            ParserUtils.eatElement(builder, IDENTIFIER);
            packageName.done(IDENTIFIER);

            ParserUtils.getToken(builder, oDOT);
        }

        ParserUtils.getToken(builder, mIDENT, "identifier.expected");

        anonymousType.done(TYPE_STRUCT_FIELD_ANONYMOUS_TYPE);
        
        return true;
    }
}
