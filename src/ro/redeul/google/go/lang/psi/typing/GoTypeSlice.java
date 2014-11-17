package ro.redeul.google.go.lang.psi.typing;

import com.intellij.openapi.project.Project;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;

public class GoTypeSlice extends GoAbstractType implements GoType {

    private Project project;
    private GoType elementType;

    public GoTypeSlice(Project project, GoType elementType) {
        this.project = project;
        this.elementType = elementType;
    }

    public GoTypeSlice(GoPsiTypeSlice type) {
        this(type.getProject(), GoTypes.getInstance(type.getProject()).fromPsiType(type.getElementType()));
    }

    @Override
    public boolean isIdentical(GoType type) {
        if ( !(type instanceof GoTypeSlice) )
            return false;

        GoTypeSlice otherSlice = (GoTypeSlice)type;

        return elementType.isIdentical(otherSlice.getElementType());
    }

    @Override
    public boolean isAssignableFrom(GoType source) {
        return super.isAssignableFrom(source);
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitSlice(this);
    }

    public GoType getElementType() {
        return elementType;
    }

    @Override
    public String toString() {
        return String.format("[]%s", getElementType());
    }

    public GoType getKeyType() {
        return GoTypes.getInstance(project).getBuiltin(GoTypes.Builtin.Int);
    }
}
