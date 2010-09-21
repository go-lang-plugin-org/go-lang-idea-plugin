package ro.redeul.google.go.lang.psi.processors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.toplevel.GoImportSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 21, 2010
 * Time: 4:24:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImportedPackagesCollectorProcessor extends BaseScopeProcessor {

    List<String> imports = new ArrayList<String>();

    public boolean execute(PsiElement element, ResolveState state) {

        if ( element instanceof GoImportSpec ) {
            processImport((GoImportSpec) element);
        }

        return true;
    }

    private void processImport(GoImportSpec importSpec) {

        GoPackageReference packageReference = importSpec.getPackageReference();

        if ( packageReference == null ) {
            imports.add(importSpec.getImportPath());
            return;
        }

        if ( packageReference.isLocal() || packageReference.isBlank() ) {
            return;
        }

        imports.add(packageReference.getString());
    }

    public String[] getPackageImports() {
        return imports.toArray(new String[imports.size()]);
    }
}
