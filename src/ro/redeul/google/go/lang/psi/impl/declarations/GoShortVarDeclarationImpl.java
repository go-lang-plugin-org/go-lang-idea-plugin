package ro.redeul.google.go.lang.psi.impl.declarations;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.declarations.GoShortVarDeclaration;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/31/11
 * Time: 11:28 PM
 */
public class GoShortVarDeclarationImpl extends GoVarDeclarationImpl implements GoShortVarDeclaration {

    public GoShortVarDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

}
