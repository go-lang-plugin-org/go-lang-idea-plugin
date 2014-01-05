/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.statements.select;

import ro.redeul.google.go.lang.psi.GoDocumentedPsiElement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;

public interface GoSelectStatement extends GoStatement, GoDocumentedPsiElement {

    GoSelectCommClause[] getCommClauses();

}
