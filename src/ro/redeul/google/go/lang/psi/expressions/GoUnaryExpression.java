package ro.redeul.google.go.lang.psi.expressions;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static ro.redeul.google.go.lang.lexer.GoTokenTypes.*;

public interface GoUnaryExpression extends GoExpr {

    enum Op implements Operator {
        None(null),
        Plus(oPLUS), Minus(oMINUS),
        Not(oNOT), Xor(oBIT_XOR),
        Pointer(oMUL), Address(oBIT_AND),
        Channel(oSEND_CHANNEL);

        final IElementType elementType;

        Op(IElementType elementType) {
            this.elementType = elementType;
        }

        @Override
        public int precedence() {
            return 6;
        }

        @Override
        public IElementType tokenType() {
            return null;
        }
    }

    @NotNull
    Op getOp();

    GoExpr getExpression();
}
