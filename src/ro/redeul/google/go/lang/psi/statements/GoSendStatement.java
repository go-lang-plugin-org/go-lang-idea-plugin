/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.statements;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoSimpleStatement;

public interface GoSendStatement extends GoSimpleStatement {

    @NotNull
    GoExpr getChannelExpr();

    @NotNull
    GoExpr getValueExpr();

}
