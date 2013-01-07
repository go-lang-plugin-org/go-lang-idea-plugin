/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.impl.statements.select;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoSendStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClauseSend;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class GoSelectCommClauseSendImpl extends GoPsiElementBase
    implements GoSelectCommClauseSend {

    public GoSelectCommClauseSendImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoSendStatement getSendStatement() {
        return findChildByClass(GoSendStatement.class);
    }

    @Override
    public GoStatement[] getStatements() {
        return findChildrenByClass(GoStatement.class);
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
