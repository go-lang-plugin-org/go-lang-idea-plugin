package ro.redeul.google.go.lang.psi.typing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeArray;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

public class GoTypes {

    public static final Pattern PRIMITIVE_TYPES_PATTERN =
        Pattern.compile("" +
                            "bool|error|byte|rune|uintptr|string|char|" +
                            "(int|uint)(8|16|32|64)?|" +
                            "float(32|64)|" +
                            "complex(64|128)");

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

    public static GoType fromPsiType(GoPsiType psiType) {
        return psiType.accept(new GoTypeMakerVisitor());
    }

    public static GoType[] fromPsiType(GoPsiType[] type) {
        return new GoType[] { GoType.Unknown };
    }

    private static class GoTypeMakerVisitor extends GoElementVisitorWithData<GoType> {
        @Override
        public void visitArrayType(GoPsiTypeArray psiType) {
            data = new GoTypeArray(psiType);
        }

        @Override
        public void visitTypeName(GoPsiTypeName psiTypeName) {
            data = new GoTypeName(psiTypeName);
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
        public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
           visitFunctionType(declaration);
        }
    }
}
