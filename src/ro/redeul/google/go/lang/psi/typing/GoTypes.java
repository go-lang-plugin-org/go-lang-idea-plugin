package ro.redeul.google.go.lang.psi.typing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.intellij.util.Function;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeArray;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeChannel;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.services.GoPsiManager;

public class GoTypes {

    public static final Pattern PRIMITIVE_TYPES_PATTERN =
        Pattern.compile("" +
                            "bool|error|byte|rune|uintptr|string|char|" +
                            "(int|uint)(8|16|32|64)?|" +
                            "float(32|64)|" +
                            "complex(64|128)");

    public static <T extends GoType> T resolveTo(GoType type, Class<T> targetType) {
        while ( type != null && type != GoType.Unknown && ! targetType.isAssignableFrom(type.getClass()) ) {
            if ( type instanceof GoTypeName ) {
                type = ((GoTypeName)type).getDefinition();
            } else {
                type = GoType.Unknown;
            }
        }

        return targetType.cast(type);
    }

    public enum Builtin {
        Bool, Byte, Complex64, Complex128, Error, Float32, Float64,
        Int, Int8, Int16, Int32, Int64, Rune, String,
        uInt, uInt8, uInt16, uInt32, uInt64, uIntPtr
    }

    static Map<Builtin, GoType> cachedTypes = new HashMap<Builtin, GoType>();

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

    public static GoType fromPsiType(final GoPsiType psiType) {
        if ( psiType == null)
            return GoType.Unknown;

        return GoPsiManager.getInstance(psiType.getProject()).getType(psiType, new Function<GoPsiElement, GoType[]>() {
            @Override
            public GoType[] fun(GoPsiElement goPsiElement) {
                return new GoType[] {
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
    }
}
