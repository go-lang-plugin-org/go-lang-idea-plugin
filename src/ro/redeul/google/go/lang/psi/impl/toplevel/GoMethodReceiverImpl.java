/*
 * Copyright 2012 Midokura Europe SARL
 */
package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.types.GoTypeName;

/**
 * // TODO: Explain yourself.
 *
 * @author Mihai Claudiu Toader <mtoader@midokura.com>
 *         Date: 6/10/12
 */
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
    public GoTypeName getTypeName() {
        return findChildByClass(GoTypeName.class);
    }
}
