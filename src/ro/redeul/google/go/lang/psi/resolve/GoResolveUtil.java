package ro.redeul.google.go.lang.psi.resolve;

import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.GoQualifiedNameElement;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 21, 2010
 * Time: 4:49:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoResolveUtil {
    public static boolean inSamePackage(GoQualifiedNameElement qualifiedElement, GoImportDeclaration importSpec) {
        GoPackageReference importedPackageReference = importSpec.getPackageReference();
        GoPackageReference elementReference = qualifiedElement.getPackageReference();

        // import "a"; var x a.T;
        if ( importedPackageReference == null && elementReference != null
                && elementReference.getString().equals(defaultPackageNameFromImport(importSpec.getImportPath())) ) {
            return true;
        }

        // import . "a"; var x T; // T is defined inside package a
        if ( importedPackageReference != null && importedPackageReference.isLocal() && elementReference == null ) {
            return true;
        }

        // import x "a"; var x.T;
        if ( importedPackageReference != null && elementReference != null && elementReference.getString().equals(importedPackageReference.getString())) {
            return true;
        }

        return false;
    }

    public static String defaultPackageNameFromImport(String importPath) {
        return GoPsiUtils.findDefaultPackageName(GoPsiUtils.cleanupImportPath(importPath));
    }

}
