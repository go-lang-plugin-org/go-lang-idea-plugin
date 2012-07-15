package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.scope.util.PsiScopesUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypes;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

public class GoLiteralExpressionImpl extends GoExpressionBase
    implements GoLiteralExpression {

    public GoLiteralExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralExpression(this);
    }

    @Override
    public GoLiteral getLiteral() {
        return findChildByClass(GoLiteral.class);
    }

    @NotNull
    @Override
    public GoType[] getType() {
        GoNamesCache namesCache = GoNamesCache.getInstance(getProject());

        switch (getLiteral().getType()) {
            case Bool:
                return new GoType[]{
                    GoTypes.getBuiltin(GoTypes.Builtin.Bool, namesCache)
                };

            case Int:
                return new GoType[]{
                    GoTypes.getBuiltin(GoTypes.Builtin.Int, namesCache)
                };

            case Float:
                return new GoType[]{
                    GoTypes.getBuiltin(GoTypes.Builtin.Float32, namesCache)
                };

            case Char:
                return new GoType[]{
                    GoTypes.getBuiltin(GoTypes.Builtin.Rune, namesCache)
                };
            case ImaginaryInt:
            case ImaginaryFloat:
                return new GoType[]{
                    GoTypes.getBuiltin(GoTypes.Builtin.Complex64, namesCache)
                };
            case RawString:
            case InterpretedString:
                return new GoType[]{
                    GoTypes.getBuiltin(GoTypes.Builtin.String, namesCache)
                };
            case Function:
                GoLiteralFunction literalFunction = (GoLiteralFunction)getLiteral();
                return new GoType[] {
                    literalFunction
                };
            case Identifier:
                GoLiteralIdentifier identifier = (GoLiteralIdentifier) getLiteral();

                PsiElement resolved = GoPsiUtils.resolveSafely(identifier, PsiElement.class);
                if (resolved == null) {
                    return GoType.EMPTY_ARRAY;
                }

                if (resolved.getParent() instanceof GoVarDeclaration) {
                    GoVarDeclaration varDeclaration = (GoVarDeclaration)resolved.getParent();
                    if ( varDeclaration.getIdentifiersType() != null) {
                        return new GoType[] {
                            varDeclaration.getIdentifiersType()
                        };
                    }
                }

                if (resolved.getParent() instanceof GoFunctionParameter) {
                    GoFunctionParameter functionParameter = (GoFunctionParameter)resolved.getParent();
                    if ( functionParameter.getType() != null) {
                        return new GoType[] {
                            functionParameter.getType()
                        };
                    }
                }

                if (resolved.getParent() instanceof GoFunctionDeclaration) {
                    GoFunctionDeclaration functionDeclaration = (GoFunctionDeclaration)resolved.getParent();
                    return new GoType[] {
                        functionDeclaration
                    };
                }
                return GoType.EMPTY_ARRAY;

            default:
                return GoType.EMPTY_ARRAY;
        }
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor
                                           processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return
            PsiScopesUtil.walkChildrenScopes(this,
                                             processor, state,
                                             lastParent, place);
    }

    @Override
    public PsiReference getReference() {
        return null;
    }
}
