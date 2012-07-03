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
public class StructType implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!ParserUtils.lookAhead(builder, kSTRUCT))
            return null;

        PsiBuilder.Marker marker = builder.mark();

        ParserUtils.getToken(builder, kSTRUCT);

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
        return TYPE_STRUCT;
    }

    private static boolean parseFieldDeclaration(PsiBuilder builder, GoParser parser) {

        if ( builder.getTokenType() == wsNLS || builder.getTokenType() == pRCURLY || builder.getTokenType() == oSEMI || builder.eof()) {
            return true;
        }

        boolean isAnonymous = false;

        PsiBuilder.Marker fieldDeclaration = builder.mark();

        int identifiersCount = parser.parseIdentifierList(builder, false);

        if ( identifiersCount == 1 && (builder.getTokenType() == wsNLS || builder.getTokenType() == litSTRING || builder.getTokenType() == oDOT) ) {
            fieldDeclaration.rollbackTo();
            fieldDeclaration = builder.mark();
            isAnonymous = true;
        }

        if ( identifiersCount == 0 && ParserUtils.lookAhead(builder, oMUL, mIDENT) ) {
            isAnonymous = true;
        }

        parser.parseType(builder);

        if ( builder.getTokenType() == litSTRING ) {
            ParserUtils.eatElement(builder, IDENTIFIER);
        }

        fieldDeclaration.done(isAnonymous ? TYPE_STRUCT_FIELD_ANONYMOUS : TYPE_STRUCT_FIELD );

        return true;
    }

//    private static boolean parseAnonymousField(PsiBuilder builder, GoParser parser) {
//
//        PsiBuilder.Marker anonymousType = builder.mark();
//
//        if ( builder.getTokenType() == oMUL ) {
//            ParserUtils.eatElement(builder, TYPE_STRUCT_FIELD_ADDRESS);
//        }
//
//        if ( ParserUtils.lookAhead(builder, mIDENT, oDOT) )  {
//            PsiBuilder.Marker packageName = builder.mark();
//            ParserUtils.eatElement(builder, IDENTIFIER);
//            packageName.done(IDENTIFIER);
//
//            ParserUtils.getToken(builder, oDOT);
//        }
//
//        ParserUtils.getToken(builder, mIDENT, "identifier.expected");
//
//        anonymousType.done(TYPE_STRUCT_FIELD_ANONYMOUS);
//
//        return true;
//    }
}
