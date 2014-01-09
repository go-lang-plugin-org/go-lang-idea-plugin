package ro.redeul.google.go.lang.psi.expressions.binary;

import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.psi.expressions.Operator;

import static ro.redeul.google.go.lang.lexer.GoTokenTypes.*;

public interface GoMultiplicativeExpression extends GoBinaryExpression<GoMultiplicativeExpression.Op> {

    enum Op implements Operator {
        None(null),
        Mul(oMUL), Quotient(oQUOTIENT), Remainder(oREMAINDER),
        ShiftLeft(oSHIFT_LEFT), ShiftRight(oSHIFT_RIGHT),
        BitAnd(oBIT_AND), BitClear(oBIT_CLEAR);

        IElementType elementType;

        Op(IElementType elementType) {
            this.elementType = elementType;
        }

        @Override
        public int precedence() {
            return 5;
        }

        @Override
        public IElementType tokenType() {
            return elementType;
        }
    }
}
