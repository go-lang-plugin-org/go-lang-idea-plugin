package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 12:50:44 PM
 */
public class GoPsiTypeSliceImpl extends GoPsiTypeImpl implements GoPsiTypeSlice {

    public GoPsiTypeSliceImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoPsiType getElementType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitSliceType(this);
    }

    @NotNull
    @Override
    public String getLookupTailText() {
        return String.format("[]%s", getElementType().getLookupTailText());
    }
}
