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
import ro.redeul.google.go.lang.psi.typing.GoTypeConstant;
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
        GoPrimaryExpression baseExpr = getBaseExpression();
        if (baseExpr == null)
            return GoType.EMPTY_ARRAY;

        GoType baseCallType[] = baseExpr.getType();

        GoType[] goTypes = GoTypes.visitFirstType(baseCallType, new TypeVisitor<GoType[]>(GoType.EMPTY_ARRAY) {

            @Override
            public GoType[] visitFunction(GoTypeFunction type) {
                return type.getResultTypes();
            }

            @Override
            public GoType[] visitName(GoTypeName type) {
                GoExpr args[] = getArguments();
                if ( args.length != 1 )
                    return new GoType[]{type};

                GoType argType[] = args[0].getType();
                if ( argType.length != 1)
                    return new GoType[] { type };

                return new GoType[]{argType[0].castAs(type)};
            }
        });

        if (goTypes != null) return goTypes;

        //try type convert
        //TODO finish this.
        return GoType.EMPTY_ARRAY;

//
//
//        PsiElement reference = resolveSafely(getBaseExpression(), PsiElement.class);
//        if (reference != null) {
//
//            PsiElement parent = reference.getParent();
//            if (parent instanceof GoMethodDeclaration) {
//                GoMethodDeclaration declaration = (GoMethodDeclaration) parent;
//                return GoTypes.fromPsiType(declaration.getReturnType());
//            }
//
//            if (parent instanceof GoFunctionDeclaration) {
//                GoFunctionDeclaration declaration = (GoFunctionDeclaration) parent;
//                return GoTypes.fromPsiType(declaration.getReturnType());
//            }
//
//            if (parent instanceof GoVarDeclaration) {
//
//                GoLiteralIdentifier[] identifiers = ((GoVarDeclaration) parent).getIdentifiers();
//                int i;
//                for (i = 0; i < identifiers.length; i++) {
//                    if (identifiers[i].getText().equals(getBaseExpression().getText())) {
//                        break;
//                    }
//                }
//                if (i < identifiers.length)
//                    return resolveVarTypes((GoVarDeclaration) parent, identifiers[i], i);
//
//            }
//        }
//
//        GoPrimaryExpression baseExpression = this.getBaseExpression();
//        if (baseExpression instanceof GoParenthesisedExpression) {
//            GoType[] types = getBaseExpression().getType();
//            if (types.length != 0) {
//                GoType type = types[0];
//                if (type != null) {
//                    if (type instanceof GoTypePsiBacked) {
//                        GoPsiType psiType = ((GoTypePsiBacked) type).getPsiType();
//                        psiType = GoTypeUtils.resolveToFinalType(psiType);
//                        if (psiType instanceof GoPsiTypeFunction) {
//                            return GoUtil.getFuncCallTypes((GoPsiTypeFunction) psiType);
//                        }
//                    }
//                }
//            }
//        }
//
//        if (baseExpression instanceof GoLiteralExpression
//                && ((GoLiteralExpression) baseExpression).getLiteral() instanceof GoLiteralFunction) {
//            return GoUtil.getFuncCallTypes((GoPsiTypeFunction) ((GoLiteralExpression) baseExpression).getLiteral());
//        }

//        return GoType.EMPTY_ARRAY;
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
