package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeStatement;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.hasPrevSiblingOfType;

public class GoForWithRangeStatementImpl extends GoForStatementImpl
    implements GoForWithRangeStatement
{
    public GoForWithRangeStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpr getKey() {
        return findChildByClass(GoExpr.class, 0);
    }

    @Override
    public GoExpr getValue() {
        GoExpr[] expressions = findChildrenByClass(GoExpr.class);

        if (expressions.length > 2) {
            return expressions[1];
        }

        if (expressions.length == 2 &&
            !hasPrevSiblingOfType(expressions[1], GoTokenTypes.kRANGE)) {
            return expressions[1];
        }

        return null;
    }

    @Override
    public boolean isDeclaration() {
        return findChildByType(GoElementTypes.oVAR_ASSIGN) != null;
    }

    @Override
    public GoExpr getRangeExpression() {
        GoExpr[] expressions = findChildrenByClass(GoExpr.class);

        if (expressions.length > 2) {
            return expressions[2];
        }

        if (expressions.length == 2 &&
            hasPrevSiblingOfType(expressions[1], GoTokenTypes.kRANGE)) {
            return expressions[1];
        }

        return null;
    }

    @Override
    public GoType[] getKeyType() {
        GoExpr rangeExpression = getRangeExpression();
        if (rangeExpression == null ) {
            return GoType.EMPTY_ARRAY;
        }

        GoType[] rangeType = rangeExpression.getType();
        if (rangeType.length == 0) {
            return GoType.EMPTY_ARRAY;
        }

        return
            new GoType.Visitor<GoType[]>(GoType.EMPTY_ARRAY) {
                @Override
                protected void visitTypeArray(GoTypeArray array) {
                    setData(new GoType[] {
                        GoTypes.getBuiltin(
                            GoTypes.Builtin.Int,
                                           GoNamesCache.getInstance(getProject()))
                    });
                }

                @Override
                public void visitTypePointer(GoTypePointer pointer) {
                    setData(
                        new GoType.Visitor<GoType[]>(GoType.EMPTY_ARRAY){
                            @Override
                            protected void visitTypeArray(GoTypeArray array) {
                                setData(new GoType[] {
                                    GoTypes.getBuiltin(
                                        GoTypes.Builtin.Int,
                                        GoNamesCache.getInstance(getProject()))
                                });
                            }

                            @Override
                            public void visitTypeSlice(GoTypeSlice slice) {
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
                public void visitTypeSlice(GoTypeSlice slice) {
                    setData(new GoType[] {
                        GoTypes.getBuiltin(
                            GoTypes.Builtin.Int,
                            GoNamesCache.getInstance(getProject()))
                    });
                }

                @Override
                public void visitTypeName(GoTypeName name) {
                    if (name.getName().equals("string")) {
                        setData(new GoType[] {
                            GoTypes.getBuiltin(
                                GoTypes.Builtin.Int,
                                GoNamesCache.getInstance(getProject()))
                        });
                    }
                }

                @Override
                public void visitTypeMap(GoTypeMap map) {
                    setData(new GoType[] {map.getKeyType()});
                }

                @Override
                public void visitTypeChannel(GoTypeChannel channel) {
                    setData(new GoType[] {channel.getElementType()});
                }
            }.visit(rangeType[0]);
    }

    @Override
    public GoType[] getValueType() {
        GoExpr rangeExpression = getRangeExpression();
        if (rangeExpression == null ) {
            return GoType.EMPTY_ARRAY;
        }

        GoType[] rangeType = rangeExpression.getType();
        if (rangeType.length == 0) {
            return GoType.EMPTY_ARRAY;
        }

        return
            new GoType.Visitor<GoType[]>(GoType.EMPTY_ARRAY) {
                @Override
                protected void visitTypeArray(GoTypeArray array) {
                    setData(new GoType[] {array.getElementType()});
                }

                @Override
                public void visitTypeSlice(GoTypeSlice slice) {
                    setData(new GoType[] {slice.getElementType()});
                }

                @Override
                public void visitTypePointer(GoTypePointer pointer) {
                    setData(new GoType.Visitor<GoType[]>(GoType.EMPTY_ARRAY) {
                        @Override
                        protected void visitTypeArray(GoTypeArray array) {
                            setData(new GoType[] {array.getElementType()});
                        }

                        @Override
                        public void visitTypeSlice(GoTypeSlice slice) {
                            setData(new GoType[] {slice.getElementType()});
                        }
                    }.visit(pointer.getTargetType()));
                }

                @Override
                public void visitTypeName(GoTypeName name) {
                    if (name.getName().equals("string")) {
                        setData(new GoType[] {
                            GoTypes.getBuiltin(
                                GoTypes.Builtin.Rune,
                                GoNamesCache.getInstance(getProject()))
                        });
                    }
                }

                @Override
                public void visitTypeMap(GoTypeMap map) {
                    setData(new GoType[] {map.getElementType()});
                }
            }.visit(rangeType[0]);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitForWithRange(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {

        if ( isDeclaration() )  {
            if (getValue() != null ) {
                if ( ! getValue().processDeclarations(processor, state, null, place))
                    return false;
            }

            if (getKey() != null ) {
                if ( ! getKey().processDeclarations(processor, state, null, place))
                    return false;
            }
        }

        return true;
    }
}
