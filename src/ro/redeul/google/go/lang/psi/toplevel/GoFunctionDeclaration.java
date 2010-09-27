package ro.redeul.google.go.lang.psi.toplevel;

import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 26, 2010
 * Time: 2:32:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoFunctionDeclaration extends GoPsiElement {

    String getFunctionName();

    boolean isMain();

    GoBlockStatement getBlock();
}
