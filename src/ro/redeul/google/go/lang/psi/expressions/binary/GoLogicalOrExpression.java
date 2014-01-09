package ro.redeul.google.go.lang.psi.expressions.binary;

import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.psi.expressions.Operator;

import static ro.redeul.google.go.lang.lexer.GoTokenTypes.*;

public interface GoLogicalOrExpression extends GoBinaryExpression<GoLogicalOrExpression.Op> {

    enum Op implements Operator {
        None(null), CondOr(oCOND_OR);

        IElementType elementType;

        Op(IElementType elementType) {
            this.elementType = elementType;
        }

        @Override
        public int precedence() {
            return 1;
        }

        @Override
        public IElementType tokenType() {
            return elementType;
        }
    }

}
