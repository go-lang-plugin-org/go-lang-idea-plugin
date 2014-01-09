package ro.redeul.google.go.lang.psi.expressions.binary;

import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.psi.expressions.Operator;

import static ro.redeul.google.go.lang.lexer.GoTokenTypes.*;

public interface GoRelationalExpression extends GoBinaryExpression<GoRelationalExpression.Op> {

    enum Op implements Operator {
        None(null),
        Eq(oEQ), NotEq(oNOT_EQ),
        Less(oLESS), LessOrEq(oLESS_OR_EQUAL),
        Greater(oGREATER), GreaterOrEq(oGREATER_OR_EQUAL);

        IElementType elementType;

        Op(IElementType elementType) {
            this.elementType = elementType;
        }

        @Override
        public int precedence() {
            return 3;
        }

        @Override
        public IElementType tokenType() {
            return elementType;
        }
    }
}
