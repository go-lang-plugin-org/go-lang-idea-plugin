package ro.redeul.google.go.lang.psi;

import com.intellij.psi.PsiFile;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 7:57:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoFile extends PsiFile, GoPsiElement {

    GoPackageDeclaration getPackage();

    GoImportDeclaration[] getImportDeclarations();

    GoFunctionDeclaration[] getFunctions();

    GoMethodDeclaration[] getMethods();

    GoFunctionDeclaration getMainFunction();
}
