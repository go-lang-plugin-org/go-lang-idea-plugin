/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;

public interface GoSendStatement extends GoSimpleStatement {

    GoExpr getChannelExpr();

    GoExpr getValueExpr();

}
