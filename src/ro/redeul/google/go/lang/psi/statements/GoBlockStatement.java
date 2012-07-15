package ro.redeul.google.go.lang.psi.statements;

/**
 * User: mtoader
 * Date: Sep 2, 2010
 * Time: 3:14:17 PM
 */
public interface GoBlockStatement extends GoStatement {

    GoStatement[] getStatements();

}
