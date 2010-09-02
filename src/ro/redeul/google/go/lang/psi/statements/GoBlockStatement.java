package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 2, 2010
 * Time: 3:14:17 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoBlockStatement extends GoStatement {

    GoStatement[] getStatements();

}
