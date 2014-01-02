package ro.redeul.google.go.lang.parser.parsing.types;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

import static ro.redeul.google.go.lang.parser.parsing.util.ParserUtils.completeStatement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 25, 2010
 * Time: 2:52:27 AM
 */
class StructType implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (!ParserUtils.lookAhead(builder, kSTRUCT, pLCURLY))
            return null;

        PsiBuilder.Marker marker = builder.mark();

        ParserUtils.getToken(builder, kSTRUCT);
        ParserUtils.getToken(builder, pLCURLY);

        while (!builder.eof() && !ParserUtils.lookAhead(builder, pRCURLY))
            if (!parseFieldDeclaration(builder, parser))
                break;

        ParserUtils.getToken(builder, pRCURLY);
        marker.done(TYPE_STRUCT);
        return TYPE_STRUCT;
    }

    private static boolean parseFieldDeclaration(PsiBuilder builder, GoParser parser) {

        if (builder.eof() || ParserUtils.lookAhead(builder, GoTokenTypeSets.EOS))
            return true;

        boolean isAnonymous = false;

        PsiBuilder.Marker fieldDeclaration = builder.mark();

        int identifiersCount = parser.parseIdentifierList(builder, false);

        if (identifiersCount == 1 &&
            (ParserUtils.lookAhead(builder, GoTokenTypeSets.EOS) ||
                ParserUtils.lookAhead(builder, GoTokenTypeSets.litSTRING) ||
                ParserUtils.lookAhead(builder, GoTokenTypeSets.oDOT) ||
                ParserUtils.lookAhead(builder, pRCURLY))) {
            fieldDeclaration.rollbackTo();
            fieldDeclaration = builder.mark();
            isAnonymous = true;
        }

        if (identifiersCount == 0 && ParserUtils.lookAhead(builder, oMUL, mIDENT)) {
            isAnonymous = true;
        }

        if (parser.parseType(builder) == null) {
            fieldDeclaration.rollbackTo();
            return false;
        }
//        parser.parseType(builder);

        if (builder.getTokenType() == litSTRING) {
            ParserUtils.eatElement(builder, IDENTIFIER);
        }

        completeStatement(builder, fieldDeclaration, isAnonymous
            ? TYPE_STRUCT_FIELD_ANONYMOUS
            : TYPE_STRUCT_FIELD);
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
