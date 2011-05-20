package ro.redeul.google.go.lang.psi.processors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import groovy.lang.StringWriterIOException;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.resolve.GoResolveUtil;
import ro.redeul.google.go.lang.psi.toplevel.GoImportSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 21, 2010
 * Time: 4:24:00 AM
 */
public class ImportedPackagesCollectorProcessor extends BaseScopeProcessor {

    List<GoImportSpec> imports = new ArrayList<GoImportSpec>();

    public boolean execute(PsiElement element, ResolveState state) {

        if ( element instanceof GoImportSpec ) {
            processImport((GoImportSpec) element);
        }

        return true;
    }

    private void processImport(GoImportSpec importSpec) {
        imports.add(importSpec);
    }

    public List<GoImportSpec> getPackageImportSpecs() {
        return imports;
    }

    public String[] getPackageImports() {

        List<String> packageImports = new ArrayList<String>();

        for (GoImportSpec importSpec : imports) {
            GoPackageReference packageReference = importSpec.getPackageReference();

            if ( packageReference == null ) {
                packageImports.add(importSpec.getImportPath());
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
