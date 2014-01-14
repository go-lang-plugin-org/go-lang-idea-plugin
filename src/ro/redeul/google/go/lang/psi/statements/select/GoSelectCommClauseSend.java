/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.statements.select;

import ro.redeul.google.go.lang.psi.statements.GoSendStatement;

public interface GoSelectCommClauseSend extends GoSelectCommClause {
    GoSendStatement getSendStatement();
}
