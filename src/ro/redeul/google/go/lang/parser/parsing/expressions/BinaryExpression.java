package ro.redeul.google.go.lang.parser.parsing.expressions;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

/**
 * User: mtoader
 * Date: Jul 28, 2010
 * Time: 3:02:31 AM
 */
class BinaryExpression implements GoElementTypes {

    private final IElementType elementType;
    private final TokenSet operators;

    private static final BinaryExpression[] precedence = {
        new BinaryExpression(LOG_OR_EXPRESSION, OPS_LOG_OR),
        new BinaryExpression(LOG_AND_EXPRESSION, OPS_LOG_AND),
        new BinaryExpression(REL_EXPRESSION, OPS_REL),
        new BinaryExpression(ADD_EXPRESSION, OPS_ADD),
        new BinaryExpression(MUL_EXPRESSION, OPS_MUL),
    };

    public static boolean parse(PsiBuilder builder, GoParser parser) {
        return parse(builder, parser, 0);
    }

    private static boolean parse(PsiBuilder builder, GoParser parser, int level) {

        PsiBuilder.Marker marker = builder.mark();
        if ( ! UnaryExpression.parse(builder, parser) ) {
            marker.rollbackTo();
            return false;
        }

        boolean processedOperator = true;

        while ( processedOperator && GoTokenTypeSets.BINARY_OPERATORS.contains(builder.getTokenType()) ) {

            processedOperator = false;

            for ( int i = level; i < precedence.length; i++ ) {
                if ( precedence[i].operators.contains(builder.getTokenType()) ) {
                    ParserUtils.getToken(builder, builder.getTokenType());
                    parse(builder, parser, i + 1);

                    marker.done(precedence[i].elementType);
                    marker = marker.precede();
                    processedOperator = true;
                    break;
                }
            }
        }

        marker.drop();

        return true;
    }

    private BinaryExpression(IElementType elementType, TokenSet operators) {
        this.elementType = elementType;
        this.operators = operators;
    }
}
