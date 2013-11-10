package ro.redeul.google.go.lang.psi.impl.statements;

import java.util.HashMap;
import java.util.Map;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoAssignmentStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static ro.redeul.google.go.lang.lexer.GoTokenTypes.*;

public class GoAssignmentStatementImpl extends GoPsiElementBase
    implements GoAssignmentStatement {

    private static final Map<IElementType, Op> operatorsMap =
        new HashMap<IElementType, Op>() {
            {
                put(oPLUS_ASSIGN, Op.PlusEq);
                put(oMINUS_ASSIGN, Op.MinusEq);
                put(oBIT_OR_ASSIGN, Op.BitOrEq);
                put(oBIT_XOR_ASSIGN, Op.BitXorEq);

                put(oMUL_ASSIGN, Op.MulEq);
                put(oQUOTIENT_ASSIGN, Op.QuotientEq);
                put(oREMAINDER_ASSIGN, Op.RemainderEq);
                put(oSHIFT_LEFT_ASSIGN, Op.ShiftLeftEq);
                put(oSHIFT_RIGHT_ASSIGN, Op.ShiftRightEq);
                put(oBIT_AND_ASSIGN, Op.BitAndEq);
                put(oBIT_CLEAR_ASSIGN, Op.BitClearEq);

                put(oASSIGN, Op.Assign);
            }
        };

    public GoAssignmentStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpressionList getLeftSideExpressions() {
        return findChildByClass(GoExpressionList.class, 0);
    }

    @Override
    public GoExpressionList getRightSideExpressions() {
        return findChildByClass(GoExpressionList.class, 1);
    }

    @Override
    @NotNull
    public Op getOperator() {
        PsiElement element = findChildByType(GoTokenTypeSets.ASSIGN_OPERATORS);

        if (element == null)
            return Op.Null;

        Op op = operatorsMap.get(element.getNode().getElementType());

        return op != null ? op : Op.Null;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitAssignment(this);
    }
}
