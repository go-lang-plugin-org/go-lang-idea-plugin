package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/31/11
 * Time: 11:23 PM
 */
public interface GoShortVarDeclaration extends GoVarDeclaration, GoSimpleStatement {
    public GoLiteralIdentifier[] getDeclarations();
}
