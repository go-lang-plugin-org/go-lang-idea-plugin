package ro.redeul.google.go.lang.psi.impl.types.struct;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.types.GoTypePointer;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 12:28 PM
 */
public class GoTypeStructAnonymousFieldImpl extends GoPsiElementBase implements GoTypeStructAnonymousField {

    public GoTypeStructAnonymousFieldImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoType getType() {
        return findChildByClass(GoType.class);
    }

    @Override
    public String getFieldName() {
        GoType type = getType();
        if ( type instanceof GoTypeName) {
            return ((GoTypeName)type).getName();
        }

        if (type instanceof GoTypePointer) {
            return ((GoTypePointer)type).getTargetType().getName();
        }

        return "";
    }

    @Override
    public String getName() {
        return getFieldName();
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeStructAnonymousField(this);
    }

    @Override
    public String getPresentationTypeText() {
        return getType().getText();
    }

    @Override
    public String getPresentationTailText() {
        return " (anonymous)";
    }
}
