package ro.redeul.google.go.compilation;

import java.util.ArrayList;
import java.util.List;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 8/21/11
 * Time: 12:02 PM
 */
public class GoFileMetadata {
    private String packageName;

    private boolean main;

    private List<String> imports;

    public GoFileMetadata(GoFile file) {
        packageName = file.getPackage().getPackageName();
        main = file.getMainFunction() != null;

        imports = new ArrayList<String>();

        GoImportDeclarations[] importDeclarations = file.getImportDeclarations();

        for (GoImportDeclarations importDeclaration : importDeclarations) {
            GoImportDeclaration[] importSpecs = importDeclaration.getDeclarations();

            for (GoImportDeclaration importSpec : importSpecs) {
                GoLiteralString importPath = importSpec.getImportPath();

                if ( importPath != null ) {
                    imports.add(importPath.getValue());
                }
            }
        }
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isMain() {
        return main;
    }

    public List<String> getImports() {
        return imports;
    }
}
