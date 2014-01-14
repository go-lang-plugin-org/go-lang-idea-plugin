/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.impl.statements.select;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClause;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

public class GoSelectStatementImpl extends GoPsiElementBase
    implements GoSelectStatement {

    public GoSelectStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoSelectCommClause[] getCommClauses() {
        return findChildrenByClass(GoSelectCommClause.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitSelectStatement(this);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitStatementSelect(this, data);
    }
}
