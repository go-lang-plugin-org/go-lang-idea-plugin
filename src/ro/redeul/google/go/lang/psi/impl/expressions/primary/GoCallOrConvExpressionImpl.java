package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoParenthesizedExprOrType;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeParenthesized;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeConstant;
import ro.redeul.google.go.lang.psi.typing.GoTypeFunction;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.typing.TypeVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;

import java.util.Arrays;

public class GoCallOrConvExpressionImpl extends GoExpressionBase implements GoCallOrConvExpression {

    public GoCallOrConvExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    protected GoType[] resolveTypes() {
        GoExpr baseExpression = getBaseExpression();

        boolean isType = baseExpression.accept(new GoElementVisitorWithData<Boolean>(false) {
            @Override
            public void visitParenthesisedExprOrType(GoParenthesizedExprOrType exprOrType) {
                setData(exprOrType.isType());
            }
        });

        if (isType)
            return computeConversionType(GoTypes.fromPsi((GoPsiTypeParenthesized) baseExpression));
        else {
            return GoTypes.get(getBaseExpression().getType()).accept(new TypeVisitor<GoType[]>(GoType.EMPTY_ARRAY) {
                @Override
                public GoType[] visitFunction(GoTypeFunction type) {
                    return computeCallType(type);
                }

                @Override
                public GoType[] visitName(GoTypeName type) {
                    return computeConversionType(type);
                }
            });
        }
    }

    protected GoType[] computeCallType(GoTypeFunction type) {
        return type.getResultTypes();
    }

    protected GoType[] computeConversionType(GoType type) {
        GoExpr args[] = getArguments();
        if (args.length != 1)
            return new GoType[]{type};

        GoType argType[] = args[0].getType();
        if (argType.length != 1 || !(argType[0] instanceof GoTypeConstant))
            return new GoType[]{type};

        GoTypeConstant constant = (GoTypeConstant) argType[0];

        if (type.canRepresent(constant)) {
            return new GoType[]{constant.retypeAs(type)};
        }

        return new GoType[]{type};
    }

    @Override
    public GoPrimaryExpression getBaseExpression() {
        return findChildByClass(GoPrimaryExpression.class);
    }

    @Override
    public GoPsiType getTypeArgument() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public GoExpr[] getArguments() {

        GoExpressionList list = findChildByClass(GoExpressionList.class);
        if (list != null) {
            return list.getExpressions();
        }

        GoExpr[] expressions = findChildrenByClass(GoExpr.class);

        if (expressions.length <= 1) {
            return GoExpr.EMPTY_ARRAY;
        }

        return Arrays.copyOfRange(expressions, 1, expressions.length);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitCallOrConvExpression(this);
    }
}
