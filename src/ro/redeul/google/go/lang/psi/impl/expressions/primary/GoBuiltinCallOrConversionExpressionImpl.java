package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.string;
import static ro.redeul.google.go.lang.psi.typing.GoTypes.Builtin.*;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 6/2/11
 * Time: 3:58 AM
 */
public class GoBuiltinCallOrConversionExpressionImpl extends GoCallOrConvExpressionImpl
        implements GoBuiltinCallOrConversionExpression {

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

    public GoBuiltinCallOrConversionExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    protected GoType[] resolveTypes() {
        GoType[] baseExprType = getBaseExpression().getType();

        if ( baseExprType.length != 1 )
            return GoType.EMPTY_ARRAY;

        if ( baseExprType[0] instanceof GoTypeFunction )
            return processBuiltinFunction(((GoTypeFunction) baseExprType[0]).getPsiType().getName());

        if ( baseExprType[0] instanceof GoTypePrimitive ) {
            GoTypePrimitive castType = (GoTypePrimitive) baseExprType[0];

            GoExpr[] arguments = getArguments();
            if ( arguments.length != 1 )
                return baseExprType;

            GoType[] argumentType = arguments[0].getType();
            if (argumentType.length != 1 || !(argumentType[0] instanceof GoTypeConstant) )
                return baseExprType;

            GoTypeConstant constantArgument = (GoTypeConstant) argumentType[0];
            if ( castType.canRepresent(constantArgument)) {
                return new GoType[] { constantArgument.retypeAs(castType)};
            }

            return new GoType[]{castType};
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
        GoExpr[] args = getArguments();

        if (functionName.equals("append")) {
            if (args.length > 1) {
                GoType[] types = args[0].getType();
                if (types.length > 0 && types[0] instanceof GoTypeSlice) {
                    GoPsiTypeSlice appendedSlice = ((GoTypeSlice) types[0]).getPsiType();
                    GoPsiType[] result = new GoPsiType[args.length];
                    result[0] = appendedSlice;
                    if (isCallWithVariadicParameter()){
                        result[1] = appendedSlice;
                        return result;
                    }
                    GoPsiType elem = appendedSlice.getElementType();
                    for (int i = 1; i < args.length; i++) {
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

    @NotNull
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
            return new GoType[]{types().getBuiltin(Int)};
        } else if (functionName.equals("complex")) {
            if (args.length > 0) {
                if (hasBuiltinType(args[0].getType(), Float64))
                    return new GoType[]{types().getBuiltin(Complex128)};

                if (hasBuiltinType(args[0].getType(), Float32))
                    return new GoType[]{types().getBuiltin(Complex64)};

//                if (hasBuiltinType(args[0].getType(), Int))
//                    return new GoType[]{types().getBuiltin(Complex128)};
            }
        } else if (functionName.matches("^(real|imag)$")) {
            if (args.length > 0) {
                if (hasBuiltinType(args[0].getType(), Complex128))
                    return new GoType[]{types().getBuiltin(Float64)};

                if (hasBuiltinType(args[0].getType(), Complex64))
                    return new GoType[]{types().getBuiltin(Float32)};
            }
        } else if (functionName.equals("make")) {
            if (typeArg != null)
                return new GoType[]{types().fromPsiType(typeArg)};
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

    private boolean hasBuiltinType(GoType[] types, final GoTypes.Builtin builtin) {
        return ContainerUtil.find(types, new Condition<GoType>() {
            @Override
            public boolean value(GoType goType) {
                if ( goType.isIdentical(types().getBuiltin(builtin)) )
                    return true;

                if ( goType instanceof GoTypeConstant ) {
                    GoTypeConstant typeConstant = (GoTypeConstant) goType;
                    return GoTypes.getInstance(getProject()).getBuiltin(builtin).canRepresent(typeConstant);
                }

                return false;
            }
        }) != null;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitBuiltinCallExpression(this);
    }
}

