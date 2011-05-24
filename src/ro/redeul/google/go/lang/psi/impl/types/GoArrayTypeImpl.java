package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoArrayType;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 30, 2010
 * Time: 9:07:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoArrayTypeImpl extends GoPsiElementBase implements GoArrayType {

    public GoArrayTypeImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoType getElementType() {
        return findChildByClass(GoType.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitArrayType(this);
    }
}
