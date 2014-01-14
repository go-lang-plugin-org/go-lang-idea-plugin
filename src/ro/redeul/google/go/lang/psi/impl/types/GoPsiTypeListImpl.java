package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeList;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

/**
 * <p/>
 * Created on Jan-13-2014 22:52
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoPsiTypeListImpl extends GoPsiElementBase implements GoPsiTypeList {

    public GoPsiTypeListImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public GoPsiType[] getTypes() {
        return getElements();
    }

    @NotNull
    @Override
    public GoPsiType[] getElements() {
        return findChildrenByClass(GoPsiType.class);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitTypeList(this, data);
    }
}
