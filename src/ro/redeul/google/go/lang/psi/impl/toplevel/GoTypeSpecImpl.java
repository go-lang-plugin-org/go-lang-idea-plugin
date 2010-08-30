package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 30, 2010
 * Time: 9:01:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoTypeSpecImpl extends GoPsiElementImpl implements GoTypeSpec {

    public GoTypeSpecImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoTypeName getTypeName() {
        return findChildByClass(GoTypeName.class);
    }

    public GoType getType() {
        return findChildByClass(GoType.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeSpec(this);
    }
}
