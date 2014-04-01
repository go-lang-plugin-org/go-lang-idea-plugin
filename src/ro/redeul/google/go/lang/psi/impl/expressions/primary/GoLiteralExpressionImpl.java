package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralComposite;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.patterns.GoElementPatterns;
import ro.redeul.google.go.lang.psi.resolve.references.BuiltinCallOrConversionReference;
import ro.redeul.google.go.lang.psi.resolve.references.CallOrConversionReference;
import ro.redeul.google.go.lang.psi.resolve.references.VarOrConstReference;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeAndVarsStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeClause;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeGuard;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.services.GoPsiManager;
import ro.redeul.google.go.util.GoUtil;

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
                            GoTypes.getBuiltin(Builtin.Float64, namesCache)
                    };

                case Char:
                    return new GoType[]{
                            GoTypes.getBuiltin(Builtin.Rune, namesCache)
                    };

                case ImaginaryInt:
                case ImaginaryFloat:
                    return new GoType[]{
                            GoTypes.getBuiltin(Builtin.Complex128, namesCache)
                    };

                case RawString:
                case InterpretedString:
                    if (literal.getNode().getElementType() == GoElementTypes.LITERAL_CHAR){
                        return new GoType[]{
                                GoTypes.getBuiltin(Builtin.Rune, namesCache)
                        };
                    } else {
                        return new GoType[]{
                                GoTypes.getBuiltin(Builtin.String, namesCache)
                        };
                    }

                case Function:
                    return new GoType[]{
                            GoTypes.fromPsiType((GoLiteralFunction) literal)
                    };

                case Identifier:
                    GoLiteralIdentifier identifier = (GoLiteralIdentifier) literal;

                    PsiElement resolved = GoUtil.ResolveReferece(identifier);
                    if (resolved == null) {
                        return GoType.EMPTY_ARRAY;
                    }

                    PsiElement parent = resolved.getParent();
                    if (parent instanceof GoVarDeclaration) {
                        GoVarDeclaration varDeclaration = (GoVarDeclaration) parent;
                        GoType identifierType = varDeclaration.getIdentifierType((GoLiteralIdentifier) resolved);

                        if (identifierType == null)
                            return GoType.EMPTY_ARRAY;

                        return new GoType[]{identifierType};
                    }

                    if (parent instanceof GoConstDeclaration) {
                        GoPsiType identifiersType = ((GoConstDeclaration) parent).getIdentifiersType();
                        if (identifiersType == null)
                            return GoType.EMPTY_ARRAY;
                        return new GoType[]{GoTypes.fromPsiType(identifiersType)};
                    }

                    if (parent instanceof GoFunctionParameter) {
                        GoFunctionParameter functionParameter = (GoFunctionParameter) parent;
                        if (functionParameter.getType() != null) {
                            return new GoType[]{
                                    GoTypes.fromPsiType(functionParameter.getType())
                            };
                        }
                    }

                    if (parent instanceof GoMethodReceiver) {
                        GoMethodReceiver receiver =
                                (GoMethodReceiver) parent;

                        if (receiver.getType() != null) {
                            return new GoType[]{
                                    GoTypes.fromPsiType(receiver.getType())
                            };
                        }
                    }

                    if (parent instanceof GoFunctionDeclaration) {
                        GoFunctionDeclaration functionDeclaration =
                                (GoFunctionDeclaration) parent;

                        return new GoType[]{
                                GoTypes.fromPsiType(functionDeclaration)
                        };
                    }

                    if (parent instanceof GoSwitchTypeGuard) {
                        GoSwitchTypeGuard guard = (GoSwitchTypeGuard) parent;
                        GoSwitchTypeStatement switchStatement = (GoSwitchTypeStatement) guard.getParent();
                        TextRange litRange = literal.getTextRange();
                        for (GoSwitchTypeClause clause : switchStatement.getClauses()) {
                            TextRange clauseTextRange = clause.getTextRange();
                            if (clauseTextRange.contains(litRange)) {
                                return GoTypes.fromPsiType(clause.getTypes());
                            }
                        }
                    }

                    if (GoElementPatterns.VAR_IN_FOR_RANGE.accepts(resolved)) {
                        GoForWithRangeAndVarsStatement statement = (GoForWithRangeAndVarsStatement) parent;

                        if (statement.getKey() == resolved) {
                            return statement.getKeyType();
                        } else if (statement.getValue() == resolved) {
                            return statement.getValueType();
                        }
                    }
                    return GoType.EMPTY_ARRAY;
                case Composite:
                    GoLiteralComposite composite = (GoLiteralComposite) literal;
                    GoPsiType literalType = composite.getLiteralType();
                    if (literalType == null) {
                        return GoType.EMPTY_ARRAY;
                    }
                    return new GoType[]{
                            GoTypes.fromPsiType(literalType)
                    };

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

                if (identifier.isIota()) {
                    return true;
                }

                PsiElement resolved = GoPsiUtils.resolveSafely(identifier, PsiElement.class);

                if (resolved == null)
                    return false;

                PsiElement constDecl = resolved.getParent();
                if (constDecl instanceof GoConstDeclaration) {
                    GoPsiType identifiersType = ((GoConstDeclaration) constDecl).getIdentifiersType();
                    //Type was specified
                    if (identifiersType != null) {
                        return ((GoPsiTypeName) identifiersType).isPrimitive();
                    }
                    //If not check the expressions
                    for (GoExpr goExpr : ((GoConstDeclaration) constDecl).getExpressions()) {
                        if (goExpr instanceof GoBinaryExpression || goExpr instanceof GoUnaryExpression) {
                            if (!goExpr.isConstantExpression())
                                return false;
                        }
                    }
                    return true;
                }

        }

        return false;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return GoPsiScopesUtil.walkChildrenScopes(this, processor, state, lastParent, place);
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
            return refs(
                    new CallOrConversionReference(this),
                    new VarOrConstReference((GoLiteralIdentifier) this.getLiteral()));

        return super.getReferences();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
