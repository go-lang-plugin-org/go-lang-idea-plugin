package ro.redeul.google.go.lang.psi.typing;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.utils.GoTokenSets;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getAs;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveTypeSpec;

public class GoTypeName extends GoTypePsiBacked<GoPsiType> implements GoType {

    private static final Logger LOG = Logger.getInstance(GoTypeName.class);


    public GoTypeName(GoTypeNameDeclaration declaration) {
        super(declaration);
    }

    public GoTypeName(GoPsiTypeName psiType) {
        super(psiType);
    }

    @NotNull
    public GoType findUnderlyingType(GoTypeNameDeclaration declaration) {
        GoTypeSpec typeSpec = declaration.getTypeSpec();

        GoPsiType type = typeSpec.getType();
        if ( type instanceof GoPsiTypeName ) {
            return types().fromPsiType(type).underlyingType();
        }

        return this;
    }

    @Override
    public boolean isIdentical(GoType type) {
        return this == type || (type instanceof GoTypeName && getName().equals(((GoTypeName) type).getName()));
    }

    @Nullable
    public GoTypeSpec getDefinition() {
        return getPsiType().accept(new GoElementVisitorWithData<GoTypeSpec>(null) {
            @Override
            public void visitTypeNameDeclaration(GoTypeNameDeclaration declaration) {
                setData(declaration.getTypeSpec());
            }

            @Override
            public void visitTypeName(GoPsiTypeName typeName) {
                setData(GoPsiUtils.resolveSafely(typeName, GoTypeSpec.class));
            }
        });
    }

    @NotNull
    @Override
    public GoType underlyingType() {
        return getPsiType().accept(new GoElementVisitorWithData<GoType>(this) {
            @Override
            public void visitTypeNameDeclaration(GoTypeNameDeclaration declaration) {
                setData(getUnderlyingType(declaration.getTypeSpec()));
            }

            private GoType getUnderlyingType(GoTypeSpec typeSpec) {
                if ( typeSpec.getType() instanceof GoPsiTypeName )
                    return GoTypes.fromPsi(typeSpec.getType());

                return GoTypeName.this;
            }

            @Override
            public void visitTypeName(GoPsiTypeName typeName) {
                GoTypeNameDeclaration nameDeclaration = GoPsiUtils.resolveSafely(typeName, GoTypeNameDeclaration.class);
                if ( nameDeclaration != null )
                    visitTypeNameDeclaration(nameDeclaration);
            }
        });
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitName(this);
    }

    public String getName() {
        return getPsiType().getName();
    }

    @Override
    public String toString() {
        return getName();
    }
}
