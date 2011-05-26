package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.codeInsight.navigation.actions.GotoTypeDeclarationAction;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoPointerType;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/26/11
 * Time: 11:53 PM
 */
public class GoPointerTypeImpl extends GoPsiElementBase implements GoPointerType {

    public GoPointerTypeImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitPointerType(this);
    }

    @Override
    public GoType getTargetType() {
        return findChildByClass(GoType.class);
    }

    @Override
    public GoPsiElement[] getMembers() {
        GoType targetType = getTargetType();

        return targetType != null ? getTargetType().getMembers() : new GoPsiElement[0];
    }
}
