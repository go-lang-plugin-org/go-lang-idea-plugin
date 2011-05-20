package ro.redeul.google.go.lang.psi.impl.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiExpression;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.GoPsiExpression;
import ro.redeul.google.go.lang.psi.expressions.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 10:58 PM
 */
public class GoSelectorExpressionImpl extends GoPsiExpressionImpl implements GoSelectorExpression {

    public GoSelectorExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(GoElementVisitor visitor) {
//        visitor.visitIdentifier(this);
    }

    @Override
    protected GoType resolveType() {
        return null;
    }

    @Override
    public GoPsiExpression getExpressionContext() {
        return findChildByClass(GoPsiExpression.class);
    }


}
