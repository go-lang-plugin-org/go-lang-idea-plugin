package ro.redeul.google.go.lang.psi.typing;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.GoParenthesizedExprOrType;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.*;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.services.GoPsiManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class GoTypes extends AbstractProjectComponent {

    private GoTypes(Project project) {
        super(project);
    }

    public static GoTypes getInstance(Project project) {
        return project.getComponent(GoTypes.class);
    }

    public static final Pattern PRIMITIVE_TYPES_PATTERN =
            Pattern.compile("" +
                    "bool|error|byte|rune|uintptr|string|char|" +
                    "(int|uint)(8|16|32|64)?|" +
                    "float(32|64)|" +
                    "complex(64|128)");

    @Nullable
    private static <T extends GoType> T resolveTo(GoType type, Class<T> targetType) {

        GoType target = type;
        while (target != null && target != GoType.Unknown && !targetType.isAssignableFrom(target.getClass())) {
            if (target instanceof GoTypeName) {
                GoTypeSpec typeSpec = ((GoTypeName) target).getDefinition();
                target = typeSpec != null ? fromPsi(typeSpec.getType()) : GoType.Unknown;
            } else {
                target = GoType.Unknown;
            }
        }

        return (target != null && targetType.isAssignableFrom(target.getClass())) ? targetType.cast(target) : null;
    }

    public static GoType makeSlice(Project project, GoType type) {
        return new GoTypeSlice(project, type);
    }

    public static GoType makePointer(GoType type) {
        return new GoTypePointer(type);
    }

    public static GoType makePointer(GoPsiType argumentType) {
        return new GoTypePointer(fromPsi(argumentType));
    }

    @NotNull
    public static GoType get(GoType[] types) {
        return types == null || types.length != 1 || types[0] == null ? GoType.Unknown : types[0];
    }

    public static GoType[] getPackageType(GoImportDeclaration declaration) {
        GoPackage goPackage = declaration.getPackage();
        return goPackage != null ? new GoType[]{new GoTypePackage(goPackage)} : GoType.EMPTY_ARRAY;
    }

    /**
     * checks type assignability according to the spec: {@linkplain http://golang.org/ref/spec#Assignability}
     */
    public static boolean isAssignableFrom(GoType dstType, GoType srcVarType) {

        if (dstType == null || srcVarType == null)
            return false;

        // x's type is identical to T.
        if (srcVarType.isIdentical(dstType))
            return true;

        // x's type V and T have identical underlying types and at least one of V or T is not a named type.
        GoType srcUnderlying = srcVarType.underlyingType();
        GoType dstUnderlying = dstType.underlyingType();

        if ((!isNamedType(srcVarType) || !isNamedType(dstType)) &&
                srcUnderlying.isIdentical(dstUnderlying))
            return true;

        // T is an interface type and x implements T.
        if (dstUnderlying instanceof GoTypeInterface) {
            GoTypeInterface typeInterface = dstType.underlyingType(GoTypeInterface.class);
            if (typeInterface != null) return typeInterface.isImplementedBy(srcVarType);

        }

        // x is a bidirectional channel value, T is a channel type, x's type V and T have identical element types, and at least one of V or T is not a named type.
        if ((!isNamedType(srcVarType) || !isNamedType(dstType)) &&
                srcUnderlying instanceof GoTypeChannel && dstUnderlying instanceof GoTypeChannel) {
            GoTypeChannel dstChannel = (GoTypeChannel) dstUnderlying;
            GoTypeChannel srcChannel = (GoTypeChannel) srcUnderlying;

            return srcChannel.getChannelType() == GoTypeChannel.ChannelType.Bidirectional &&
                    srcChannel.getElementType().isIdentical(dstChannel.getElementType());
        }

        // x is the predeclared identifier nil and T is a pointer, function, slice, map, channel, or interface type.
        if (srcVarType == GoType.Nil)
            return
                    dstUnderlying instanceof GoTypePointer || dstUnderlying instanceof GoTypeFunction ||
                            dstUnderlying instanceof GoTypeSlice || dstUnderlying instanceof GoTypeMap ||
                            dstUnderlying instanceof GoTypeChannel || dstUnderlying instanceof GoTypeInterface;

        // x is an untyped constant representable by a value of type T.
        if (srcVarType instanceof GoTypeConstant) {
            GoTypeConstant constant = (GoTypeConstant) srcVarType;
            if (constant.getType() == GoType.Unknown)
                return dstUnderlying.canRepresent(constant);
        }

        return false;
    }

    private static boolean isNamedType(GoType type) {
        if (type instanceof GoTypeName)
            return true;

        if (type instanceof GoTypeConstant) {
            GoTypeConstant constant = (GoTypeConstant) type;
            return constant.getType() instanceof GoTypeName;
        }

        return false;
    }

    public static GoType constant(GoTypeConstant.Kind kind, Object value) {
        return new GoTypeConstant(kind, value);
    }

    public static GoType constant(GoTypeConstant.Kind kind, Object value, GoType type) {
        GoTypeConstant typeConstant = new GoTypeConstant(kind, value);
        typeConstant.setType(type);
        return typeConstant;
    }

    public static boolean areIdentical(GoType[] types, GoType[] oTypes) {
        if (types.length != oTypes.length)
            return false;

        for (int i = 0, typesLength = types.length; i < typesLength; i++)
            if (!types[i].isIdentical(oTypes[i]))
                return false;

        return true;
    }

    public enum Builtin {
        Bool, Byte, Complex64, Complex128, Error, Float32, Float64,
        Int, Int8, Int16, Int32, Int64, Rune, String,
        uInt, uInt8, uInt16, uInt32, uInt64, uIntPtr
    }

    public static final Map<Builtin, GoType> cachedTypes = new HashMap<Builtin, GoType>();

    public GoType getBuiltin(Builtin builtinType) {

        GoNamesCache namesCache = GoNamesCache.getInstance(myProject);

        GoType type = cachedTypes.get(builtinType);
        if (type == null) {
            Collection<GoFile> files = namesCache.getFilesByPackageName("builtin");

            for (GoFile file : files) {
                for (GoTypeDeclaration typeDeclaration : file.getTypeDeclarations()) {
                    for (GoTypeSpec spec : typeDeclaration.getTypeSpecs()) {
                        if (spec != null) {
                            String name = spec.getName();
                            if (name != null && name.equals(builtinType.name().toLowerCase())) {
                                cachedTypes.put(builtinType, fromPsiType(spec.getType()));
                            }
                        }
                    }
                }
            }
        }

        return cachedTypes.get(builtinType);
    }

    @NotNull
    public static GoType fromPsi(final GoPsiType psiType) {
        return psiType != null ? getInstance(psiType.getProject()).fromPsiType(psiType) : GoType.Unknown;
    }

    @NotNull
    public GoType fromPsiType(final GoPsiType psiType) {
        if (psiType == null)
            return GoType.Unknown;

        return GoPsiManager.getInstance(psiType.getProject()).getOrCompute(psiType, new Function<GoPsiType, GoType>() {
            @Override
            public GoType fun(GoPsiType psiType) {
                return psiType.accept(new GoTypeMakerVisitor());
            }
        });
    }

    public static GoType[] fromPsiType(GoPsiType[] psiTypes) {
        GoType types[] = new GoType[psiTypes.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = fromPsi(psiTypes[i]);
        }

        return types;
    }

    public static <T> T visitFirstType(GoType[] types, TypeVisitor<T> visitor) {
        return visitFirstType(types, visitor, false);
    }

    public static <T> T visitFirstType(GoType[] types, TypeVisitor<T> visitor, boolean visitUnderlying) {
        if (types != null && types.length >= 1 && types[0] != null)
            return visitor.visit(visitUnderlying ? types[0].underlyingType() : types[0]);

        return visitor.getData();
    }

    private static class GoTypeMakerVisitor extends GoElementVisitorWithData<GoType> {
        private GoTypeMakerVisitor() {
            data = GoType.Unknown;
        }

        @Override
        public void visitChannelType(GoPsiTypeChannel psiType) {
            data = new GoTypeChannel(psiType);
        }

        @Override
        public void visitArrayType(GoPsiTypeArray psiType) {
            data = new GoTypeArray(psiType);
        }

        @Override
        public void visitTypeName(GoPsiTypeName psiType) {
            GoTypeNameDeclaration declaration = GoPsiUtils.resolveSafely(psiType.getIdentifier(), GoTypeNameDeclaration.class);
            if (declaration != null)
                data = declaration.accept(this);
        }

        @Override
        public void visitTypeNameDeclaration(GoTypeNameDeclaration declaration) {
            data = declaration.isPrimitive() ? new GoTypePrimitive(declaration) : new GoTypeName(declaration);
        }

        @Override
        public void visitMapType(GoPsiTypeMap type) {
            data = new GoTypeMap(type);
        }

        @Override
        public void visitSliceType(GoPsiTypeSlice psiType) {
            data = new GoTypeSlice(psiType);
        }

        @Override
        public void visitPointerType(GoPsiTypePointer psiType) {
            data = new GoTypePointer(psiType);
        }

        @Override
        public void visitStructType(GoPsiTypeStruct psiType) {
            data = new GoTypeStruct(psiType);
        }

        @Override
        public void visitFunctionType(GoPsiTypeFunction type) {
            data = new GoTypeFunction(type);
        }

        @Override
        public void visitInterfaceType(GoPsiTypeInterface type) {
            data = new GoTypeInterface(type);
        }

        @Override
        public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
            visitFunctionType(declaration);
        }

        @Override
        public void visitMethodDeclaration(GoMethodDeclaration declaration) {
            visitFunctionType(declaration);
        }

        @Override
        public void visitFunctionLiteral(GoLiteralFunction declaration) {
            visitFunctionType(declaration);
        }

        @Override
        public void visitParenthesisedExprOrType(GoParenthesizedExprOrType exprOrType) {
            GoPsiType innerType = exprOrType.getInnerType();
            if (innerType != null)
                innerType.accept(this);
        }
    }

    public static GoTypeStruct resolveToStruct(GoPsiType type) {
        return resolveToStruct(fromPsi(type));
    }

    private static GoTypeStruct resolveToStruct(@Nullable GoType type) {
        return new TypeVisitor<GoTypeStruct>() {
            @Override
            public GoTypeStruct visitStruct(GoTypeStruct type) { return type; }

            @Override
            public GoTypeStruct visitName(GoTypeName type) { return visit(type.underlyingType()); }

            @Override
            public GoTypeStruct visitPointer(GoTypePointer type) { return visit(type.getTargetType()); }
        }.visit(type);
    }

    @Nullable
    public static <T extends GoType> T dereferenceTo(Class<T> targetClass, @Nullable GoType type) {
        GoType target = dereference(type);

        return targetClass.isInstance(target) ? targetClass.cast(target) : null;
    }

    @Nullable
    public static GoType dereference(@Nullable GoType type) {
        return dereference(type, 1);
    }

    @Nullable
    private static GoType dereference(@Nullable GoType type, final int dereferences) {
        return new TypeVisitor<GoType>(null) {
            int myDereferences = dereferences;

            @Override
            public GoType visitName(GoTypeName type) {
                return type == type.underlyingType() ? type : type.underlyingType().accept(this);
            }

            @Override
            public GoType visitPointer(GoTypePointer type) {
                myDereferences--;
                return myDereferences >= 0 ? type.getTargetType().accept(this) : type;
            }

            @Override
            public GoType visitArray(GoTypeArray type) { return type; }

            @Override
            public GoType visitFunction(GoTypeFunction type) { return type; }

            @Override
            public GoType visitChannel(GoTypeChannel type) { return type; }

            @Override
            public GoType visitSlice(GoTypeSlice type) { return type; }

            @Override
            public GoType visitMap(GoTypeMap type) { return type; }

            @Override
            public GoType visitPackage(GoTypePackage type) { return type; }

            @Override
            public GoType visitStruct(GoTypeStruct type) { return type; }

            @Override
            public GoType visitInterface(GoTypeInterface type) { return type; }

            @Override
            public GoType visitNil(GoType type) { return type; }

            @Override
            public GoType visitUnknown(GoType type) { return type; }

            @Override
            public GoType visitVariadic(GoTypeVariadic type) { return type; }

        }.visit(type);
    }

    public static String getRepresentation(GoType type, final GoFile view) {
        return type.accept(new RepresentingTypeVisitor(view), new StringBuilder()).toString();
    }
}
