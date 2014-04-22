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
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeMap;
import ro.redeul.google.go.lang.psi.typing.GoTypeSlice;
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

        if (reference == null) {
            return processBuiltinFunction(this.getBaseExpression().getText());
        }

        if (reference.getParent() instanceof GoMethodDeclaration) {
            GoMethodDeclaration declaration = (GoMethodDeclaration) reference.getParent();
            return GoTypes.fromPsiType(declaration.getReturnType());
        }

        if (reference.getParent() instanceof GoFunctionDeclaration) {
            GoFunctionDeclaration declaration =
                    (GoFunctionDeclaration) reference.getParent();

            if (BUILTIN_FUNCTION.accepts(declaration))
                return processBuiltinFunction(declaration.getFunctionName());
        }

        return GoType.EMPTY_ARRAY;
    }

    @Override
    public GoPsiType[] getArgumentsType() {
        PsiElement reference = resolveSafely(getBaseExpression(),
                PsiElement.class);

        if (reference == null) {
            return processArgumentsType(this.getBaseExpression().getText());
        }

        if (reference.getParent() instanceof GoFunctionDeclaration) {
            GoFunctionDeclaration declaration =
                    (GoFunctionDeclaration) reference.getParent();

            if (BUILTIN_FUNCTION.accepts(declaration))
                return processArgumentsType(declaration.getFunctionName());
        }

        return GoPsiType.EMPTY_ARRAY;
    }

    private GoPsiType[] processArgumentsType(String functionName) {
        GoNamesCache namesCache = GoNamesCache.getInstance(getProject());

        GoExpr[] args = getArguments();
        GoPsiType typeArg = getTypeArgument();

        if (functionName.equals("append")) {
            if (args.length > 1) {
                GoType[] types = args[0].getType();
                if (types.length > 0 && types[0] instanceof GoTypeSlice) {
                    GoPsiTypeSlice appendedSlice = ((GoTypeSlice) types[0]).getPsiType();
                    GoPsiType[] result = new GoPsiType[args.length];
                    result[0] = appendedSlice;
                    GoPsiType elem = appendedSlice.getElementType();
                    for (int i = 1; i < args.length; i++){
                        result[i] = elem;
                    }
                    return result;
                }
            }
        } else if (functionName.equals("copy")) {
            if (args.length == 2) {
                GoType[] types = args[0].getType();
                if (types.length > 0 && types[0] instanceof GoTypeSlice) {
                    GoPsiTypeSlice copiedSlice = ((GoTypeSlice) types[0]).getPsiType();
                    return new GoPsiType[]{copiedSlice, copiedSlice};
                }
            }
        } else if (functionName.equals("delete")) {
            if (args.length == 2) {
                GoType[] types = args[0].getType();
                if (types.length > 0 && types[0] instanceof GoTypeMap) {
                    GoPsiTypeMap map = ((GoTypeMap) types[0]).getPsiType();
                    return new GoPsiType[]{map, map.getKeyType()};
                }
            }
        }

        return GoPsiType.EMPTY_ARRAY;
    }

    private GoType[] processBuiltinFunction(String functionName) {

        GoNamesCache namesCache = GoNamesCache.getInstance(getProject());

        GoExpr[] args = getArguments();
        GoPsiType typeArg = getTypeArgument();

        if (functionName.equals("new")) {
            if (typeArg != null) {
                return new GoType[]{
                        GoTypes.makePointer(typeArg)
                };
            }
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

                if (args[0].hasType(Int))
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
                if (types.length > 0) {
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

