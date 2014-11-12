package ro.redeul.google.go.lang.psi.impl.expressions;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 10:58 PM
 */
public abstract class GoExpressionBase extends GoPsiElementBase implements GoExpr {

    GoPsiType type;

    protected GoTypes types() {
        return GoTypes.getInstance(getProject());
    }

    protected GoExpressionBase(@NotNull ASTNode node) {
        super(node);
    }

    public String getString() {
        return getText();
    }

    @NotNull
    @Override
    public GoType[] getType() {
//        return
//            GoPsiManager.getInstance(getProject()).getType(this, new Function<GoExpressionBase, GoType[]>() {
//            @Override
//            public GoType[] fun(GoExpressionBase goExpressionBase) {
//                return resolveTypes();
//            }
//        });
        return resolveTypes();
    }

    @Override
    public boolean isConstantExpression() {
        return false;
    }

    protected GoType[] resolveTypes() {
        return GoType.EMPTY_ARRAY;
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitElement(this);
    }
}
