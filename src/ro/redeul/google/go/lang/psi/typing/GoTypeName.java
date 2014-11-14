package ro.redeul.google.go.lang.psi.typing;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveTypeSpec;

public class GoTypeName extends GoTypePsiBacked<GoPsiTypeName> implements GoType {

    private static final Logger LOG = Logger.getInstance(GoTypeName.class);

    private final String name;
    private GoType definition;

    public GoTypeName(GoPsiTypeName type) {
        super(type);
        name = type.getName();
    }

    @NotNull
    @Override
    public GoType underlyingType() {
        if ( getPsiType().isPrimitive() )
            return this;

        GoTypeSpec spec = resolveTypeSpec(getPsiType());
        if ( spec != null && spec.getType() != null)
            return types().fromPsiType(spec.getType()).underlyingType();

        return this;
    }

    @Override
    public boolean isIdentical(GoType type) {
        return this == type || (type instanceof GoTypeName && getName().equals(((GoTypeName) type).getName()));
    }

    @Nullable
    public GoTypeSpec getDefinition() {
        return GoPsiUtils.resolveTypeSpec(getPsiType());
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitName(this);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isAssignableFrom(GoType source) {
        return GoTypes.isAssignableFrom(this, source);
    }

    @Override
    public String toString() {
        return name;
    }
}
