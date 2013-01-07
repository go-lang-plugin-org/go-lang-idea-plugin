/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.statements.select;

import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;

public interface GoSelectCommClause extends GoPsiElement {

    GoStatement[] getStatements();

    boolean isDefault();

}
