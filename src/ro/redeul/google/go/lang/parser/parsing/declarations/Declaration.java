package ro.redeul.google.go.lang.parser.parsing.declarations;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 25, 2010
 * Time: 12:03:28 AM
 */
public class Declaration extends ParserUtils implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if ( lookAhead(builder, kCONST) ) {
            if ( parser.isSet(GoParser.ParsingFlag.Debug) )
                System.out.println("Parsing const");

            return ConstDeclaration.parse(builder, parser);
        }

        if ( lookAhead(builder, kVAR) ) {
            if ( parser.isSet(GoParser.ParsingFlag.Debug) )
                System.out.println("Parsing var");
            return VarDeclaration.parse(builder, parser);
        }

        if ( ParserUtils.lookAhead(builder, kTYPE) ) {
            if ( parser.isSet(GoParser.ParsingFlag.Debug) )
                System.out.println("Parsing type");
            return TypeDeclaration.parse(builder, parser);
        }

        return null;
    }
}
