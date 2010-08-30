package ro.redeul.google.go.lang.psi.impl.expressions;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 10:43:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoIdentifierImpl extends GoPsiElementImpl implements GoIdentifier {

    public GoIdentifierImpl(@NotNull ASTNode node) {
        super(node);
    }

    public String getString() {
        return getText();
    }

    public boolean isBlank() {
        return getString().equals("_");
    }

    public String toString() {
        return getString();
    }

    public void accept(GoElementVisitor visitor) {
//        visitor.visitIdentifier(this);
    }
}
