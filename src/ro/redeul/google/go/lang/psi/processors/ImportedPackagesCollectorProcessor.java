package ro.redeul.google.go.lang.psi.processors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 21, 2010
 * Time: 4:24:00 AM
 */
public class ImportedPackagesCollectorProcessor extends BaseScopeProcessor {

    private final List<GoImportDeclaration> imports = new ArrayList<GoImportDeclaration>();

    public boolean execute(@NotNull PsiElement element, ResolveState state) {

        if (element instanceof GoImportDeclaration) {
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

            if (packageReference == null) {
                GoLiteralString importPath = importSpec.getImportPath();
                if (importPath != null)
                    packageImports.add(importPath.getValue());
                continue;
            }

            if (packageReference.isLocal() || packageReference.isBlank()) {
                continue;
            }

            packageImports.add(packageReference.getString());
        }

        return packageImports.toArray(new String[packageImports.size()]);
    }
}
