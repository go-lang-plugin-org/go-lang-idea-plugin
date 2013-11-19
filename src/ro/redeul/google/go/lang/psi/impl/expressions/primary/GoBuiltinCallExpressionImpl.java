package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.string;
import static ro.redeul.google.go.lang.psi.typing.GoTypes.Builtin.*;
import static ro.redeul.google.go.lang.psi.typing.GoTypes.getBuiltin;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 6/2/11
 * Time: 3:58 AM
 */
public class GoBuiltinCallExpressionImpl extends GoCallOrConvExpressionImpl
    implements GoBuiltinCallExpression {

    private static final ElementPattern<GoFunctionDeclaration> BUILTIN_FUNCTION =
        psiElement(GoFunctionDeclaration.class)
            .withParent(
                psiElement(GoFile.class)
                    .withChild(
                        psiElement(GoPackageDeclaration.class)
                            .withText(
                                string().endsWith("builtin"))
                    )
            );

    public GoBuiltinCallExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType[] resolveTypes() {
        PsiElement reference = resolveSafely(getBaseExpression(),
                                             PsiElement.class);
        if (reference == null)
            return GoType.EMPTY_ARRAY;

        if (reference.getParent() instanceof GoMethodDeclaration) {
            GoMethodDeclaration declaration = (GoMethodDeclaration) reference.getParent();
            return GoTypes.fromPsiType(declaration.getReturnType());
        }

        if (reference.getParent() instanceof GoFunctionDeclaration) {
            GoFunctionDeclaration declaration =
                (GoFunctionDeclaration) reference.getParent();

            if (BUILTIN_FUNCTION.accepts(declaration))
                return processBuiltinFunction(declaration);
        }

        return GoType.EMPTY_ARRAY;
    }

    private GoType[] processBuiltinFunction(GoFunctionDeclaration declaration) {

        GoNamesCache namesCache = GoNamesCache.getInstance(getProject());

        String functionName = declaration.getFunctionName();
        GoExpr[] args = getArguments();
        GoPsiType typeArg = getTypeArgument();

        if (functionName.equals("new")) {
            if (typeArg != null)
                return new GoType[]{
                    GoTypes.makePointer(typeArg)
                };
        } else if (functionName.matches("^(len|cap|copy)$")) {
            return new GoType[]{
                getBuiltin(Int, namesCache)
            };
        } else if (functionName.equals("complex")) {
            if (args.length > 0) {
                if (args[0].hasType(Float32))
                    return new GoType[]{
                        getBuiltin(Complex64, namesCache)
                    };

                if (args[0].hasType(Float64))
                    return new GoType[]{
                        getBuiltin(Complex128, namesCache)
                    };
            }
        } else if (functionName.matches("^(real|imag)$")) {
            if (args.length > 0) {
                if (args[0].hasType(Complex128))
                    return new GoType[]{
                        getBuiltin(Float64, namesCache)
                    };

                if (args[0].hasType(Complex64))
                    return new GoType[]{
                        getBuiltin(Float32, namesCache)
                    };
            }
        } else if (functionName.equals("make")) {
            if (typeArg != null)
                return new GoType[]{
                    GoTypes.fromPsiType(typeArg)
                };
        } else if (functionName.equals("append")) {
            if (args.length > 1) {
                GoType[] types = args[0].getType();
                if (types.length > 1) {
                    return new GoType[]{types[0]};
                }
            }
        }

        return GoType.EMPTY_ARRAY;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitBuiltinCallExpression(this);
    }

    @Override
    public boolean isConstantExpression() {
        GoExpr[] arguments = getArguments();
        return arguments.length == 1 && arguments[0].isConstantExpression();
    }
}

