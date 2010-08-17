package ro.redeul.google.go.lang.psi.toplevel;

import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 10:54:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoImportSpec extends GoPsiElement {

    GoIdentifier getPackageName();

    String getImportPath();
}
