package ro.redeul.google.go.lang.psi.typing;

import com.intellij.openapi.diagnostic.Logger;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypePredeclared;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveTypeSpec;

public class GoTypeName extends GoTypePsiBacked<GoPsiTypeName, GoUnderlyingType> implements GoType {

    private static final Logger LOG = Logger.getInstance(GoTypeName.class);

    private final String name;
    private GoType definition;

    public GoTypeName(GoPsiTypeName type) {
        super(type);
        name = type.getName();

        setUnderlyingType(GoUnderlyingType.Undefined);

        if ( type.isPrimitive() ) {
            setUnderlyingType(GoUnderlyingTypePredeclared.getForName(name));
        } else {
            GoTypeSpec spec = resolveTypeSpec(type);
            if ( spec != null && spec.getType() != null) {
                definition = GoTypes.fromPsiType(spec.getType());
                if ( definition != null && definition.getUnderlyingType() != null )
                    setUnderlyingType(definition.getUnderlyingType());
            }
        }
    }

    @Override
    public boolean isIdentical(GoType type) {
        return this == type;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeName(this);
    }

    public String getName() {
        return name;
    }

    public GoType getDefinition() {
        return definition;
    }
}
