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

public class GoTypeName extends GoTypePsiBacked<GoTypeNameDeclaration> implements GoType {

    private static final Logger LOG = Logger.getInstance(GoTypeName.class);


    public GoTypeName(GoTypeNameDeclaration declaration) {
        super(declaration);
    }

    @Override
    public boolean isIdentical(GoType type) {
        return this == type || (type instanceof GoTypeName && getName().equals(((GoTypeName) type).getName()));
    }

    @Nullable
    public GoTypeSpec getDefinition() {
        return getPsiType().getTypeSpec();
    }

    @NotNull
    @Override
    public GoType underlyingType() {
        GoTypeSpec definition = getDefinition();
        return definition == null ? this : GoTypes.fromPsi(definition.getType()).underlyingType();
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
