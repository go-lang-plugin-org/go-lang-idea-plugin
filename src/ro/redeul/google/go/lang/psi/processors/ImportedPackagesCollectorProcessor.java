package ro.redeul.google.go.lang.psi.processors;

import java.util.ArrayList;
import java.util.List;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 21, 2010
 * Time: 4:24:00 AM
 */
public class ImportedPackagesCollectorProcessor extends BaseScopeProcessor {

    List<GoImportDeclaration> imports = new ArrayList<GoImportDeclaration>();

    public boolean execute(PsiElement element, ResolveState state) {

        if ( element instanceof GoImportDeclaration) {
            processImport((GoImportDeclaration) element);
        }

        return true;
    }

    private void processImport(GoImportDeclaration importSpec) {
        imports.add(importSpec);
    }

    public List<GoImportDeclaration> getPackageImportSpecs() {
        return imports;
    }

    public String[] getPackageImports() {

        List<String> packageImports = new ArrayList<String>();

        for (GoImportDeclaration importSpec : imports) {
            GoPackageReference packageReference = importSpec.getPackageReference();

            if ( packageReference == null ) {
                packageImports.add(importSpec.getImportPath().getValue());
                continue;
            }

            if ( packageReference.isLocal() || packageReference.isBlank() ) {
                continue;
            }

            packageImports.add(packageReference.getString());
        }

        return packageImports.toArray(new String[packageImports.size()]);
    }
}
