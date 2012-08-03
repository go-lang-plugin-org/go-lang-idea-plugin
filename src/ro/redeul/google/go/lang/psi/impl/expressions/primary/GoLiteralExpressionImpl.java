package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.resolve.references.BuiltinCallOrConversionReference;
import ro.redeul.google.go.lang.psi.resolve.references.CallOrConversionReference;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.services.GoPsiManager;
import static ro.redeul.google.go.lang.psi.typing.GoTypes.Builtin;

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


    private static class LiteralTypeCalculator
        implements Function<GoLiteralExpressionImpl, GoType[]> {
        @Override
        public GoType[] fun(GoLiteralExpressionImpl expression) {
            GoLiteral literal = expression.getLiteral();
            if (literal == null)
                return GoType.EMPTY_ARRAY;

            GoNamesCache namesCache =
                GoNamesCache.getInstance(expression.getProject());

            switch (literal.getType()) {
                case Bool:
                    return new GoType[]{
                        GoTypes.getBuiltin(Builtin.Bool, namesCache)
                    };

                case Int:
                    return new GoType[]{
                        GoTypes.getBuiltin(Builtin.Int, namesCache)
                    };

                case Float:
                    return new GoType[]{
                        GoTypes.getBuiltin(Builtin.Float32, namesCache)
                    };

                case Char:
                    return new GoType[]{
                        GoTypes.getBuiltin(Builtin.Rune, namesCache)
                    };

                case ImaginaryInt:
                case ImaginaryFloat:
                    return new GoType[]{
                        GoTypes.getBuiltin(Builtin.Complex64, namesCache)
                    };

                case RawString:
                case InterpretedString:
                    return new GoType[]{
                        GoTypes.getBuiltin(Builtin.String, namesCache)
                    };

                case Function:
                    return new GoType[]{
                        GoTypes.fromPsiType((GoLiteralFunction) literal)
                    };

                case Identifier:
                    GoLiteralIdentifier identifier = (GoLiteralIdentifier) literal;

                    PsiElement resolved = GoPsiUtils.resolveSafely(identifier,
                                                                   PsiElement.class);
                    if (resolved == null) {
                        return GoType.EMPTY_ARRAY;
                    }

                    if (resolved.getParent() instanceof GoShortVarDeclaration) {
                        GoShortVarDeclaration shortVarDeclaration = (GoShortVarDeclaration) resolved.getParent();

                        GoType identifierType = shortVarDeclaration.getIdentifierType((GoLiteralIdentifier)resolved);

                        if (identifierType == null)
                            return GoType.EMPTY_ARRAY;

                        return new GoType[]{identifierType};
                    }

                    if (resolved.getParent() instanceof GoVarDeclaration) {
                        GoVarDeclaration varDeclaration = (GoVarDeclaration) resolved
                            .getParent();
                        if (varDeclaration.getIdentifiersType() != null) {
                            return new GoType[]{
                                GoTypes.fromPsiType(
                                    varDeclaration.getIdentifiersType())
                            };
                        }
                    }

                    if (resolved.getParent() instanceof GoFunctionParameter) {
                        GoFunctionParameter functionParameter = (GoFunctionParameter) resolved
                            .getParent();
                        if (functionParameter.getType() != null) {
                            return new GoType[]{
                                GoTypes.fromPsiType(functionParameter.getType())
                            };
                        }
                    }

                    if (resolved.getParent() instanceof GoFunctionDeclaration) {
                        GoFunctionDeclaration functionDeclaration = (GoFunctionDeclaration) resolved
                            .getParent();
                        return new GoType[]{
                            GoTypes.fromPsiType(functionDeclaration)
                        };
                    }
                    return GoType.EMPTY_ARRAY;

                default:
                    return GoType.EMPTY_ARRAY;
            }
        }
    }

    private static final LiteralTypeCalculator TYPE_CALCULATOR = new LiteralTypeCalculator();

    @NotNull
    @Override
    public GoType[] getType() {
        return GoPsiManager.getInstance(getProject())
                           .getType(this, TYPE_CALCULATOR);
    }

    @Override
    public boolean isConstantExpression() {
        GoLiteral literal = getLiteral();
        if (literal == null)
            return true;

        switch (literal.getType()) {
            case Bool:
            case Char:
            case Float:
            case ImaginaryFloat:
            case ImaginaryInt:
            case Int:
            case InterpretedString:
            case RawString:
                return true;
            case Identifier:
                GoLiteralIdentifier identifier = (GoLiteralIdentifier) literal;

                if ( identifier.isIota() ){
                    return true;
                }

                PsiElement resolved =
                    GoPsiUtils.resolveSafely(identifier, PsiElement.class);

                if (resolved == null)
                    return false;

                if (resolved.getParent() instanceof GoConstDeclaration )
                    return true;

        }

        return false;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return
            PsiScopesUtil.walkChildrenScopes(this,
                                             processor, state,
                                             lastParent, place);
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {

        if (BuiltinCallOrConversionReference.MATCHER.accepts(this)) {
            if (getLiteral().getText().matches("print|println"))
                return refs(PsiReference.EMPTY_ARRAY);

            return refs(new BuiltinCallOrConversionReference(this));
        }

        if (CallOrConversionReference.MATCHER.accepts(this))
            return refs(new CallOrConversionReference(this));

        return super.getReferences();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
