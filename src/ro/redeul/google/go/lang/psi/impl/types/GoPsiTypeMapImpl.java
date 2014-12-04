package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.childAt;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 12:53:17 PM
 */
public class GoPsiTypeMapImpl extends GoPsiTypeImpl implements GoPsiTypeMap {

    public GoPsiTypeMapImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoPsiType getKeyType() {
        return childAt(0, findChildrenByClass(GoPsiType.class));
    }

    public GoPsiType getElementType() {
        return childAt(1, findChildrenByClass(GoPsiType.class));
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitMapType(this);
    }

    @Override
    public String getLookupTailText() {
        return String.format("map[%s]%s",
                             getKeyType().getLookupTailText(),
                             getElementType().getLookupTailText());
    }
}
