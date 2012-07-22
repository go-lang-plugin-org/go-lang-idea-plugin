package ro.redeul.google.go.lang.psi.types.underlying;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

public class GoUnderlyingTypePredeclared implements GoUnderlyingType {

    static Map<String, GoUnderlyingTypePredeclared> predeclaredTypes =
        new HashMap<String, GoUnderlyingTypePredeclared>();

    static {
        for (GoTypes.Builtin builtin : GoTypes.Builtin.values())
        {
            predeclaredTypes.put(builtin.name().toLowerCase(),
                                 new GoUnderlyingTypePredeclared(
                                     builtin));
        }
    }

    private GoTypes.Builtin type;

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
