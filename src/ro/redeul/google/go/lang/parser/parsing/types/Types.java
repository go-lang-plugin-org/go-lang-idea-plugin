package ro.redeul.google.go.lang.parser.parsing.types;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 25, 2010
 * Time: 2:42:28 AM
 */
public class Types implements GoElementTypes {

    public static IElementType parseTypeDeclaration(PsiBuilder builder, GoParser parser) {

        if (ParserUtils.lookAhead(builder, pLPAREN)) {

            PsiBuilder.Marker marker = builder.mark();

            ParserUtils.getToken(builder, pLPAREN);

            parseTypeDeclaration(builder, parser);

            if ( ! ParserUtils.getToken(builder, pRPAREN, "right.para.required") ) {
                ParserUtils.waitNext(builder, pRPAREN, "right.parenthesis.expected");
            }

            marker.done(TYPE_PARENTHESIZED);
            return TYPE_PARENTHESIZED;
        }

        if ( builder.getTokenType() == pLBRACK ) {
            if ( ParserUtils.lookAhead(builder, pLBRACK, pRBRACK) ) {
                return SliceType.parse(builder, parser);
            } else {
                return ArrayType.parse(builder, parser);
            }
        }

        if ( ParserUtils.lookAhead(builder, kSTRUCT, pLCURLY) ) {
            return StructType.parse(builder, parser);
        }

        if ( ParserUtils.lookAhead(builder, kINTERFACE) ) {
            return InterfaceType.parse(builder, parser);
        }

        if ( ParserUtils.lookAhead(builder, kMAP) ) {
          return parseMapType(builder, parser);
//          return MapType.parse(builder, parser);
        }

        if ( ParserUtils.lookAhead(builder, kCHAN) || ParserUtils.lookAhead(builder, oSEND_CHANNEL) ) {
            return ChanType.parse(builder, parser);
        }

        if ( ParserUtils.lookAhead(builder, kFUNC) ) {
            return FunctionType.parse(builder, parser);
        }

        if ( ParserUtils.lookAhead(builder, oMUL) ) {
            return PointerType.parse(builder, parser);
        }

        if ( ParserUtils.lookAhead(builder, mIDENT) ) {
            return parseQualifiedType(builder);
        }

        return null;
    }

    private static IElementType parseQualifiedType(PsiBuilder builder) {

        if (!ParserUtils.lookAhead(builder, mIDENT))
            return null;

        PsiBuilder.Marker marker = builder.mark();
        ParserUtils.getToken(builder, mIDENT);

        if (ParserUtils.getToken(builder, oDOT)) {
            ParserUtils.getToken(builder, mIDENT, GoBundle.message("error.identifier.expected"));
        }

        marker.done(LITERAL_IDENTIFIER);
        marker.precede().done(TYPE_NAME);

        return TYPE_NAME;
    }

    /**
     * typeName := [ "*" ] identifier .
     */
    public static boolean parseTypeName(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker typeNameMarker = builder.mark();
        PsiBuilder.Marker identifier = builder.mark();
        if ( builder.getTokenType() == mIDENT) {
            String identifierName = builder.getTokenText();
            ParserUtils.getToken(builder, mIDENT);

            if (parser.isPackageName(identifierName) && ParserUtils.lookAhead(builder, oDOT) ) {
                ParserUtils.getToken(builder, oDOT);
                ParserUtils.getToken(builder, mIDENT,
                                     GoBundle.message("identifier.expected"));
            }

            identifier.done(LITERAL_IDENTIFIER);
        } else {
            ParserUtils.wrapError(builder, "identifier.expected");
            identifier.drop();
        }

        if ( parser.isSet(GoParser.ParsingFlag.ShouldCompleteStatement))
            ParserUtils.completeStatement(builder, typeNameMarker, TYPE_NAME);
        else {
            typeNameMarker.done(TYPE_NAME);
        }
        return true;
    }

  public static IElementType parseMapType(PsiBuilder builder, GoParser parser) {

    if (!ParserUtils.lookAhead(builder, kMAP))
      return null;

    PsiBuilder.Marker marker = builder.mark();

    ParserUtils.getToken(builder, kMAP);
    ParserUtils.getToken(builder, pLBRACK, "left.bracket.expected");
    parser.parseType(builder);
    ParserUtils.getToken(builder, pRBRACK, "right.bracket.expected");
    parser.parseType(builder);

    marker.done(TYPE_MAP);
    return TYPE_MAP;
  }


  public static int parseTypeDeclarationList(PsiBuilder builder, GoParser parser) {

        PsiBuilder.Marker marker = builder.mark();

        int count = 0;
        while ( ! builder.eof() && parseTypeDeclaration(builder, parser) != null ) {
            count++;
            if ( ! ParserUtils.getToken(builder, oCOMMA) ) {
                break;
            }
        }

        if ( count > 1 ) {
            marker.done(TYPE_LIST);
        } else {
            marker.drop();
        }

        return count;
    }
}
