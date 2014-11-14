package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;

public abstract class GoAbstractForWithRangeStatementImpl<Self extends GoAbstractForWithRangeStatementImpl<Self>> extends GoForStatementImpl {

    public GoAbstractForWithRangeStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public abstract GoExpr getRangeExpression();

    public GoType[] getKeyType() {
        GoExpr rangeExpression = getRangeExpression();
        if (rangeExpression == null) {
            return GoType.EMPTY_ARRAY;
        }
        GoType goType;
        GoType[] rangeType = rangeExpression.getType();
        if (rangeType.length == 0) {
            return GoType.EMPTY_ARRAY;
        }
        goType = rangeType[0];
        if (goType instanceof GoTypeName) {
            GoPsiTypeName psiType = ((GoTypeName) goType).getPsiType();
            if (!psiType.isPrimitive()) {
                goType = GoTypes.fromPsi(GoTypeUtils.resolveToFinalType(psiType));
            }
        }

        final GoTypes types = GoTypes.getInstance(getProject());

        return new TypeVisitor<GoType[]>(GoType.EMPTY_ARRAY) {
            @Override
            public GoType[] visitArray(GoTypeArray type) {
                return new GoType[]{types.getBuiltin(GoTypes.Builtin.Int)};
            }

            @Override
            public GoType[] visitPointer(GoTypePointer pointer) {
                return new TypeVisitor<GoType[]>(GoType.EMPTY_ARRAY) {
                    @Override
                    public GoType[] visitArray(GoTypeArray type) {
                        return new GoType[]{types.getBuiltin(GoTypes.Builtin.Int)};
                    }

                    @Override
                    public GoType[] visitSlice(GoTypeSlice type) {
                        return new GoType[]{types.getBuiltin(GoTypes.Builtin.Int)};
                    }
                }.visit(pointer.getTargetType());
            }

            @Override
            public GoType[] visitSlice(GoTypeSlice type) {
                return new GoType[]{types.getBuiltin(GoTypes.Builtin.Int)};
            }

            @Override
            public GoType[] visitName(GoTypeName type) {
                if (type.getName().equals("string")) {
                    return new GoType[]{types.getBuiltin(GoTypes.Builtin.Int)};
                }

                return GoType.EMPTY_ARRAY;
            }

            @Override
            public GoType[] visitMap(GoTypeMap type) { return new GoType[]{type.getKeyType()}; }

            @Override
            public GoType[] visitChannel(GoTypeChannel type) { return new GoType[]{type.getElementType()}; }
        }.visit(goType);
    }

    public GoType[] getValueType() {
        GoExpr rangeExpression = getRangeExpression();
        if (rangeExpression == null) {
            return GoType.EMPTY_ARRAY;
        }
        GoType goType;
        GoType[] rangeType = rangeExpression.getType();
        if (rangeType.length == 0) {
            return GoType.EMPTY_ARRAY;
        }
        goType = rangeType[0];
        if (goType instanceof GoTypeName) {
            GoPsiTypeName psiType = ((GoTypeName) goType).getPsiType();
            if (!psiType.isPrimitive()) {
                goType = GoTypes.fromPsi(GoTypeUtils.resolveToFinalType(psiType));
            }
        }

        final GoTypes types = GoTypes.getInstance(getProject());
        return
                new TypeVisitor<GoType[]>(GoType.EMPTY_ARRAY) {
                    @Override
                    public GoType[] visitArray(GoTypeArray type) { return new GoType[]{type.getElementType()}; }

                    @Override
                    public GoType[] visitSlice(GoTypeSlice type) { return new GoType[]{type.getElementType()}; }

                    @Override
                    public GoType[] visitPointer(GoTypePointer pointer) {
                        return new TypeVisitor<GoType[]>(GoType.EMPTY_ARRAY) {
                            @Override
                            public GoType[] visitArray(GoTypeArray type) {
                                return new GoType[]{type.getElementType()};
                            }

                            @Override
                            public GoType[] visitSlice(GoTypeSlice type) { return new GoType[]{type.getElementType()}; }
                        }.visit(pointer.getTargetType());
                    }

                    @Override
                    public GoType[] visitName(GoTypeName type) {
                        if (type.getName().equals("string")) {
                            return new GoType[]{types.getBuiltin(GoTypes.Builtin.Rune)};
                        }
                        return GoType.EMPTY_ARRAY;
                    }

                    @Override
                    public GoType[] visitMap(GoTypeMap type) { return new GoType[]{type.getElementType()}; }
                }.visit(goType);
    }
}
