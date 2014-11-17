package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

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
public class GoBuiltinCallOrConversionExpressionImpl extends GoCallOrConvExpressionImpl implements GoBuiltinCallOrConversionExpression {

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

    private GoFunctions.Builtin kind = null;

    public GoBuiltinCallOrConversionExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected GoFunctions.Builtin getBuiltinKind() {
        if ( kind == null )
            kind = GoFunctions.getFunction(getBaseExpression().getText());

        return kind;
    }

    @Override
    protected GoType[] computeCallType(GoTypeFunction type) {
        String functionName = type.getPsiType().getName();
        if (functionName == null)
            return super.computeCallType(type);

        GoExpr[] args = getArguments();
        GoType[] argumentType = GoType.EMPTY_ARRAY;

        switch (getBuiltinKind()) {
            case None:
                return super.computeCallType(type);

            case New:
                return new GoType[]{GoTypes.makePointer(getTypeArgument())};

            case Append:
                if (args.length <= 1)
                    return new GoType[]{GoType.Unknown};

                return args[0].getType();

            case Copy:
                return new GoType[]{types().getBuiltin(Int)};

            case Delete:
                return GoType.EMPTY_ARRAY;

            case Make:
                return new GoType[]{GoTypes.fromPsi(getTypeArgument())};

            case Complex:
                // HACK: this has wrong semantics.
                if( args.length < 2)
                    return new GoType[] { types().getBuiltin(Complex128) };

                GoTypes.Builtin firstFloatSize = findFloatSize(args[0]);
                GoTypes.Builtin secondFloatSize = findFloatSize(args[1]);
                if ( firstFloatSize == Float32  && secondFloatSize == Float32)
                    return new GoType[] { types().getBuiltin(Complex64)};

                return new GoType[] { types().getBuiltin(Complex128) };
            case Real:
            case Imag:
                if( args.length < 1)
                    return new GoType[] { types().getBuiltin(Float64) };

                GoTypes.Builtin complexSize = findComplexSize(args[0]);

                switch (complexSize) {
                    case Complex128:
                        return new GoType[]{types().getBuiltin(Float64)};
                    case Complex64:
                        return new GoType[]{types().getBuiltin(Float32)};
                }

            default:
                return GoType.EMPTY_ARRAY;
        }
    }

    @Override
    public GoPsiType[] getArgumentsType() {
        PsiElement reference = resolveSafely(getBaseExpression(), PsiElement.class);

        if (reference == null) return processArgumentsType();

        if (reference.getParent() instanceof GoFunctionDeclaration) {
            GoFunctionDeclaration declaration =
                    (GoFunctionDeclaration) reference.getParent();

            if (BUILTIN_FUNCTION.accepts(declaration))
                return processArgumentsType();
        }

        return GoPsiType.EMPTY_ARRAY;
    }

    private GoPsiType[] processArgumentsType() {
        GoExpr[] args = getArguments();

        if (getBuiltinKind() == GoFunctions.Builtin.Delete) {
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

    @Override
    protected GoType[] computeConversionType(GoTypeName type) {
        return super.computeConversionType(type);
    }

    private GoTypes.Builtin findComplexSize(@Nullable GoExpr arg) {
        if ( arg == null )
            return Complex128;

        return GoTypes.visitFirstType(arg.getType(), new TypeVisitor<GoTypes.Builtin>(Float64) {
            @Override
            public GoTypes.Builtin visitPrimitive(GoTypePrimitive type) {
                switch (type.getType()) {
                    case Complex64:
                        return Complex64;
                    default:
                        return Complex128;
                }
            }

            @Override
            public GoTypes.Builtin visitConstant(GoTypeConstant constant) {
                if (constant.getType() != GoType.Unknown)
                    return constant.getType().accept(this);

                return Complex128;
            }
        }, true);
    }

    private GoTypes.Builtin findFloatSize(@Nullable GoExpr arg) {
        if ( arg == null )
            return Float64;

        return GoTypes.visitFirstType(arg.getType(), new TypeVisitor<GoTypes.Builtin>(Float64) {
            @Override
            public GoTypes.Builtin visitPrimitive(GoTypePrimitive type) {
                switch (type.getType()) {
                    case Float32:
                        return Float32;
                    default:
                        return Float64;
                }
            }

            @Override
            public GoTypes.Builtin visitConstant(GoTypeConstant constant) {
                if (constant.getType() != GoType.Unknown)
                    return constant.getType().accept(this);

                return Float64;
            }
        }, true);
    }

    private boolean hasBuiltinType(GoType[] types, final GoTypes.Builtin builtin) {
        return ContainerUtil.find(types, new Condition<GoType>() {
            @Override
            public boolean value(GoType goType) {
                if (goType.isIdentical(types().getBuiltin(builtin)))
                    return true;

                if (goType instanceof GoTypeConstant) {
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

