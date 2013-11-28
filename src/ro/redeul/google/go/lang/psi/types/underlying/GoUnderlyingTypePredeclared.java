package ro.redeul.google.go.lang.psi.types.underlying;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

import java.util.HashMap;
import java.util.Map;

public class GoUnderlyingTypePredeclared implements GoUnderlyingType {

    private static final Map<String, GoUnderlyingTypePredeclared> predeclaredTypes =
        new HashMap<String, GoUnderlyingTypePredeclared>();

    static {
        for (GoTypes.Builtin builtin : GoTypes.Builtin.values())
        {
            predeclaredTypes.put(builtin.name().toLowerCase(),
                                 new GoUnderlyingTypePredeclared(
                                     builtin));
        }
    }

    private final GoTypes.Builtin type;

    private GoUnderlyingTypePredeclared(GoTypes.Builtin type) {
        this.type = type;
    }

    @Override
    public boolean isIdentical(GoUnderlyingType other) {
        return this == other;
    }

    @Nullable
    public static GoUnderlyingTypePredeclared getForName(String name) {
        return predeclaredTypes.get(name);
    }

    public static GoUnderlyingTypePredeclared getForType(GoTypes.Builtin type) {
        return predeclaredTypes.get(type.name().toLowerCase());
    }

    @Override
    public String toString() {
        return type.name().toLowerCase();
    }
}
