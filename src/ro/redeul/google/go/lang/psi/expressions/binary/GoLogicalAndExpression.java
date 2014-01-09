package ro.redeul.google.go.lang.psi.expressions.binary;

import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.psi.expressions.Operator;

import static ro.redeul.google.go.lang.lexer.GoTokenTypes.*;
import static ro.redeul.google.go.lang.lexer.GoTokenTypes.oGREATER;
import static ro.redeul.google.go.lang.lexer.GoTokenTypes.oGREATER_OR_EQUAL;

public interface GoLogicalAndExpression extends GoBinaryExpression<GoLogicalAndExpression.Op> {

    enum Op implements Operator {
        None(null),
        CondAnd(oCOND_AND);

        IElementType elementType;

        Op(IElementType elementType) {
            this.elementType = elementType;
        }

        @Override
        public int precedence() {
            return 2;
        }

        @Override
        public IElementType tokenType() {
            return elementType;
        }
    }

}
