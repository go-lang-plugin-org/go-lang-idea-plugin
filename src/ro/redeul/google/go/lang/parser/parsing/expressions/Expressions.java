package ro.redeul.google.go.lang.parser.parsing.expressions;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

public class Expressions implements GoElementTypes {

    public static boolean parse(PsiBuilder builder, GoParser parser) {
        return BinaryExpression.parse(builder, parser);
    }

    public static int parseList(PsiBuilder builder, GoParser parser) {

        parser.setFlag(GoParser.ParsingFlag.WrapCompositeInExpression);
        int count = 0;
        do {

            if ( parse(builder, parser) ) {
                count++;
            }

            if ( ParserUtils.lookAhead(builder, GoTokenTypeSets.EOS) )
                break;

            if ( !ParserUtils.lookAhead(builder, oCOMMA))
                break;

            ParserUtils.getToken(builder, oCOMMA);
        } while ( ! builder.eof() );

        parser.unsetFlag(GoParser.ParsingFlag.WrapCompositeInExpression);
        return count;
    }

    public static boolean parsePrimary(PsiBuilder builder, GoParser parser) {
        return PrimaryExpression.parse(builder, parser);
    }
}
