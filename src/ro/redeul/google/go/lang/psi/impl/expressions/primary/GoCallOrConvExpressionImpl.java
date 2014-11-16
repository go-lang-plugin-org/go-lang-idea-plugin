package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeFunction;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypePsiBacked;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.typing.TypeVisitor;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.util.GoUtil;

import java.util.Arrays;

public class GoCallOrConvExpressionImpl extends GoExpressionBase implements GoCallOrConvExpression {

    public GoCallOrConvExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    protected GoType[] resolveTypes() {

        GoType[] baseType = getBaseExpression().getType();

        if (baseType.length != 1)
            return GoType.EMPTY_ARRAY;

        return GoTypes.visitFirstType(baseType, new TypeVisitor<GoType[]>(GoType.EMPTY_ARRAY) {
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

    protected GoType[] computeCallType(GoTypeFunction type) {
        return type.getResultTypes();
    }
    
    protected GoType[] computeConversionType(GoTypeName type) {
        GoExpr args[] = getArguments();
        if ( args.length != 1 )
            return new GoType[]{type};

        GoType argType[] = args[0].getType();
        if ( argType.length != 1 || !(argType[0] instanceof GoTypeConstant))
            return new GoType[] { type };

        GoTypeConstant constant = (GoTypeConstant) argType[0];

        if ( type.canRepresent(constant) ) {
            return new GoType[] { constant.retypeAs(type)};
        }

        return new GoType[]{type};
    }
    
    private GoType[] resolveVarTypes(GoVarDeclaration parent, GoLiteralIdentifier identifier, int i) {

        GoType identifierType = parent.getIdentifierType(identifier);
        if (identifierType != null && identifierType instanceof GoTypePsiBacked) {
            GoPsiType goPsiType = GoTypeUtils.resolveToFinalType(((GoTypePsiBacked) identifierType).getPsiType());
            if (goPsiType instanceof GoPsiTypeFunction) {
                return GoUtil.getFuncCallTypes((GoPsiTypeFunction) goPsiType);
            }
        }

        GoExpr[] expressions = parent.getExpressions();
        if (expressions.length == 1 && expressions[0] instanceof GoCallOrConvExpression) {
            GoType[] types = expressions[0].getType();
            if (i < types.length) {
                GoType type = types[i];
                if (type instanceof GoTypePsiBacked) {
                    GoPsiType goPsiType = GoTypeUtils.resolveToFinalType(((GoTypePsiBacked) type).getPsiType());
                    if (goPsiType instanceof GoPsiTypeFunction) {
                        return GoUtil.getFuncCallTypes((GoPsiTypeFunction) goPsiType);
                    }
                }
            }
        }

        if (i < expressions.length) {
            return expressions[i].getType();
        }

        return GoType.EMPTY_ARRAY;
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

    public boolean isCallWithVariadicParameter() {
        PsiElement psi1 = findLastChildByType(GoTokenTypes.oTRIPLE_DOT);

        if (psi1 != null) {
            return true;
        }

        GoExpressionList list = findChildByClass(GoExpressionList.class);
        if (list == null) {
            return false;
        }

        return list.getLastChild().getNode().getElementType() == GoTokenTypes.oTRIPLE_DOT;
    }
}
