package ro.redeul.google.go.lang.psi.impl.types.struct;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.LinkedList;
import java.util.List;

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

    public GoPsiType getType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public String getFieldName() {
        GoPsiType type = getType();
        if ( type instanceof GoPsiTypeName) {
            return type.getName();
        }

        if (type instanceof GoPsiTypePointer) {
            return ((GoPsiTypePointer)type).getTargetType().getName();
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
