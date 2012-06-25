package ro.redeul.google.go.lang.psi.impl.types.struct;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;

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

    public GoTypeName getType() {
        return findChildByClass(GoTypeName.class);
    }
}
