package ro.redeul.google.go.lang.psi.utils;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;

public class GoTypeUtils {
    // When trying to resolve the final type, what if user defined a recursive type?
    // Consider the resolving level deeper than this constant as recursive type.
    private static final int MAX_TYPE_RESOLVING_LEVEL = 500;

    /**
     * Recursively resolve type, until encounter one of the following case:
     * <ul>
     *  <li>type is null</li>
     *  <li>type is primitive</li>
     *  <li>type is pointer</li>
     *  <li>type is not a GoPsiTypeName</li>
     *  <li>resolving level is too deep</li>
     * </ul>
     * @param type type to resolve
     * @return null if resolving level is too deep, otherwise return the last resolved type.
     */
    @Nullable
    public static GoPsiType resolveToFinalType(@Nullable GoPsiType type) {
        return resolveToFinalType(type, 0);
    }

    private static GoPsiType resolveToFinalType(GoPsiType type, int level) {
        if (level > MAX_TYPE_RESOLVING_LEVEL) {
            return null;
        }

        if (!(type instanceof GoPsiTypeName)) {
            return type;
        }

        GoPsiTypeName typeName = (GoPsiTypeName) type;
        if (typeName.isReference() || typeName.isPrimitive()) {
            return type;
        }

        GoTypeSpec typeSpec = GoPsiUtils.resolveSafely(type, GoTypeSpec.class);
        if (typeSpec != null) {
            return resolveToFinalType(typeSpec.getType(), level + 1);
        }
        return type;
    }
}
