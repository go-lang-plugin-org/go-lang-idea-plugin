/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.statements.select;

import ro.redeul.google.go.lang.psi.statements.GoSendStatement;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public interface GoSelectCommClauseSend extends GoSelectCommClause {
    GoSendStatement getSendStatement();
}
