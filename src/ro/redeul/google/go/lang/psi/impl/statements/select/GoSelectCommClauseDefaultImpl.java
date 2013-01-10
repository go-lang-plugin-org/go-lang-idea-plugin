/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.impl.statements.select;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClauseDefault;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoSelectCommClauseDefaultImpl extends GoPsiElementBase
    implements GoSelectCommClauseDefault {

    public GoSelectCommClauseDefaultImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoStatement[] getStatements() {
        return findChildrenByClass(GoStatement.class);
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitSelectCommClauseDefault(this);
    }
}
