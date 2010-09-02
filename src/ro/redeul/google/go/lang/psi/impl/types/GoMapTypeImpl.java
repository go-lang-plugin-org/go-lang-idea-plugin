package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.types.GoMapType;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 2, 2010
 * Time: 12:53:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoMapTypeImpl extends GoPsiElementImpl implements GoMapType {
    public GoMapTypeImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoType getKeyType() {
        return findChildrenByClass(GoType.class)[0];
    }

    public GoType getElementType() {
        return findChildrenByClass(GoType.class)[1];
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitMapType(this);
    }
}
