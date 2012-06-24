package ro.redeul.google.go.lang.psi.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
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
                namesCache.getFilesByPackageName(
                    GoPsiUtils.cleanupImportPath("builtin"));


            for (GoFile file : files) {
                for (GoTypeDeclaration typeDeclaration : file.getTypeDeclarations()) {
                    for (GoTypeSpec spec : typeDeclaration.getTypeSpecs()) {
                        if ( spec != null && spec.getName() != null &&
                            spec.getName().equals(builtinType.name().toLowerCase())) {
                            cachedTypes.put(builtinType, spec.getType());
                            type = spec.getType();
                        }
                    }
                }
            }

        }
        return type;
    }
}
