package ro.redeul.google.go.lang.psi.toplevel;

import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 10:29:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoPackageDefinition extends GoPsiElement {

    GoIdentifier getPackage();
}
