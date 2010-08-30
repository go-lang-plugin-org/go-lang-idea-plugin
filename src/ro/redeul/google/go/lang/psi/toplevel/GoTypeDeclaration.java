package ro.redeul.google.go.lang.psi.toplevel;

import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 30, 2010
 * Time: 8:56:01 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoTypeDeclaration extends GoPsiElement {

    GoTypeSpec[] getTypeSpecs();
}
