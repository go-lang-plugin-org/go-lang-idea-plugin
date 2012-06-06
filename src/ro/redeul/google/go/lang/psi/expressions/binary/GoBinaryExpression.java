package ro.redeul.google.go.lang.psi.expressions.binary;

import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;

public interface GoBinaryExpression extends GoExpr {

    IElementType getOperator();

    GoExpr getLeftOperand();

    GoExpr getRightOperand();

}
