package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

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
                goType = GoTypes.fromPsiType(GoTypeUtils.resolveToFinalType(psiType));
            }
        }


        return
                new GoType.Visitor<GoType[]>(GoType.EMPTY_ARRAY) {
                    @Override
                    public void visitArray(GoTypeArray type) {
                        setData(new GoType[]{
                                GoTypes.getBuiltin(
                                        GoTypes.Builtin.Int,
                                        GoNamesCache.getInstance(getProject()))
                        });
                    }

                    @Override
                    public void visitPointer(GoTypePointer pointer) {
                        setData(
                                new GoType.Visitor<GoType[]>(GoType.EMPTY_ARRAY) {
                                    @Override
                                    public void visitArray(GoTypeArray type) {
                                        setData(new GoType[]{
                                                GoTypes.getBuiltin(
                                                        GoTypes.Builtin.Int,
                                                        GoNamesCache.getInstance(getProject()))
                                        });
                                    }

                                    @Override
                                    public void visitSlice(GoTypeSlice type) {
                                        setData(new GoType[]{
                                                GoTypes.getBuiltin(
                                                        GoTypes.Builtin.Int,
                                                        GoNamesCache.getInstance(getProject()))
                                        });
                                    }
                                }.visit(pointer.getTargetType())
                        );
                    }

                    @Override
                    public void visitSlice(GoTypeSlice type) {
                        setData(new GoType[]{
                                GoTypes.getBuiltin(
                                        GoTypes.Builtin.Int,
                                        GoNamesCache.getInstance(getProject()))
                        });
                    }

                    @Override
                    public void visitName(GoTypeName type) {
                        if (type.getName().equals("string")) {
                            setData(new GoType[]{
                                    GoTypes.getBuiltin(
                                            GoTypes.Builtin.Int,
                                            GoNamesCache.getInstance(getProject()))
                            });
                        }
                    }

                    @Override
                    public void visitMap(GoTypeMap type) {
                        setData(new GoType[]{type.getKeyType()});
                    }

                    @Override
                    public void visitChannel(GoTypeChannel type) {
                        setData(new GoType[]{type.getElementType()});
                    }
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
                goType = GoTypes.fromPsiType(GoTypeUtils.resolveToFinalType(psiType));
            }
        }

        return
                new GoType.Visitor<GoType[]>(GoType.EMPTY_ARRAY) {
                    @Override
                    public void visitArray(GoTypeArray type) {
                        setData(new GoType[]{type.getElementType()});
                    }

                    @Override
                    public void visitSlice(GoTypeSlice type) {
                        setData(new GoType[]{type.getElementType()});
                    }

                    @Override
                    public void visitPointer(GoTypePointer pointer) {
                        setData(new GoType.Visitor<GoType[]>(GoType.EMPTY_ARRAY) {
                            @Override
                            public void visitArray(GoTypeArray type) {
                                setData(new GoType[]{type.getElementType()});
                            }

                            @Override
                            public void visitSlice(GoTypeSlice type) {
                                setData(new GoType[]{type.getElementType()});
                            }
                        }.visit(pointer.getTargetType()));
                    }

                    @Override
                    public void visitName(GoTypeName type) {
                        if (type.getName().equals("string")) {
                            setData(new GoType[]{
                                    GoTypes.getBuiltin(
                                            GoTypes.Builtin.Rune,
                                            GoNamesCache.getInstance(getProject()))
                            });
                        }
                    }

                    @Override
                    public void visitMap(GoTypeMap type) {
                        setData(new GoType[]{type.getElementType()});
                    }
                }.visit(goType);
    }
}
