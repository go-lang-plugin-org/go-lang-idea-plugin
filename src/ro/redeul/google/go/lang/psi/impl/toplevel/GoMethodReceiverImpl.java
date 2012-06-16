package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.types.GoTypeName;

public class GoMethodReceiverImpl extends GoPsiElementBase
    implements GoMethodReceiver
{
    public GoMethodReceiverImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoLiteralIdentifier getIdentifier() {
        return findChildByClass(GoLiteralIdentifier.class);
    }

    @Override
    public boolean isReference() {
        return findChildByType(GoTokenTypes.oMUL) != null;
    }

    @Override
    public GoTypeName getTypeName() {
        return findChildByClass(GoTypeName.class);
    }
}
