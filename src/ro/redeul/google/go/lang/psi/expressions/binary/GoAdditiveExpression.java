package ro.redeul.google.go.lang.psi.expressions.binary;

import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.psi.expressions.Operator;

import static ro.redeul.google.go.lang.lexer.GoTokenTypes.*;

public interface GoAdditiveExpression extends GoBinaryExpression<GoAdditiveExpression.Op> {

    enum Op implements Operator {
        None(null), Plus(oPLUS), Minus(oMINUS), BitOr(oBIT_OR), BitXor(oBIT_XOR);

        IElementType elementType;

        Op(IElementType elementType) {
            this.elementType = elementType;
        }

        @Override
        public int precedence() {
            return 4;
        }

        @Override
        public IElementType tokenType() {
            return elementType;
        }
    }
}
