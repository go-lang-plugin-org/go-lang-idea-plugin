package ro.redeul.google.go.lang.psi.typing;

import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.*;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.services.GoPsiManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class GoTypes {

    public static final Pattern PRIMITIVE_TYPES_PATTERN =
            Pattern.compile("" +
                    "bool|error|byte|rune|uintptr|string|char|" +
                    "(int|uint)(8|16|32|64)?|" +
                    "float(32|64)|" +
                    "complex(64|128)");

    @Nullable
    public static <T extends GoType> T resolveTo(GoType type, Class<T> targetType) {

        GoType target = type;
        while (target != null && target != GoType.Unknown && !targetType.isAssignableFrom(target.getClass())) {
            if (target instanceof GoTypeName) {
                target = ((GoTypeName) target).getDefinition();
            } else {
                target = GoType.Unknown;
            }
        }

        return (target != null && targetType.isAssignableFrom(target.getClass())) ? targetType.cast(target) : null;
    }

    public static GoTypeInterface resolveToInterface(GoPsiTypeName typeName) {
        return resolveTo(fromPsiType(typeName), GoTypeInterface.class);
    }

    public static GoType makePointer(GoPsiType argumentType) {
        return new GoTypePointer(fromPsiType(argumentType));
    }

    public static GoType[] getPackageType(GoImportDeclaration declaration) {
        GoPackage goPackage = declaration.getPackage();
        return goPackage != null ? new GoType[] { new GoTypePackage(goPackage) } : GoType.EMPTY_ARRAY;
    }

    public enum Builtin {
        Bool, Byte, Complex64, Complex128, Error, Float32, Float64,
        Int, Int8, Int16, Int32, Int64, Rune, String,
        uInt, uInt8, uInt16, uInt32, uInt64, uIntPtr
    }

    public static final Map<Builtin, GoType> cachedTypes = new HashMap<Builtin, GoType>();

    public static GoType getBuiltin(Builtin builtinType, GoNamesCache namesCache) {
        GoType type = cachedTypes.get(builtinType);
        if (type == null) {
            Collection<GoFile> files =
                    namesCache.getFilesByPackageName("builtin");

            for (GoFile file : files) {
                for (GoTypeDeclaration typeDeclaration : file.getTypeDeclarations()) {
                    for (GoTypeSpec spec : typeDeclaration.getTypeSpecs()) {
                        if (spec != null) {
                            String name = spec.getName();
                            if (name != null &&
                                    name.equals(builtinType.name().toLowerCase())) {
                                cachedTypes.put(builtinType,
                                        fromPsiType(spec.getType()));
                            }
                        }
                    }
                }
            }
        }

        return cachedTypes.get(builtinType);
    }

    @NotNull
    public static GoType fromPsiType(final GoPsiType psiType) {
        if (psiType == null)
            return GoType.Unknown;

        return GoPsiManager.getInstance(psiType.getProject()).getType(psiType, new Function<GoPsiElement, GoType[]>() {
            @Override
            public GoType[] fun(GoPsiElement goPsiElement) {
                return new GoType[]{
                        psiType.accept(new GoTypeMakerVisitor())
                };
            }
        })[0];
    }

    public static GoType[] fromPsiType(GoPsiType[] psiTypes) {
        GoType types[] = new GoType[psiTypes.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = fromPsiType(psiTypes[i]);
        }

        return types;
    }

    public static <T> T visitFirstType(GoType[] types, GoType.Visitor<T> visitor) {
        if ( types != null && types.length >= 1 && types[0] != null)
            visitor.visit(types[0]);

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
            data = new GoTypeName(psiType);
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
    }

    public static GoTypeStruct resolveToStruct(GoPsiType type) {
        return resolveToStruct(fromPsiType(type));
    }

    public static GoTypeStruct resolveToStruct(@Nullable GoType type) {
        return new GoType.Visitor<GoTypeStruct>() {
            @Override
            public void visitStruct(GoTypeStruct type) { setData(type); }

            @Override
            public void visitName(GoTypeName type) { visit(type.getDefinition()); }

            @Override
            public void visitPointer(GoTypePointer type) { visit(type.getTargetType()); }
        }.visit(type);
    }
}
