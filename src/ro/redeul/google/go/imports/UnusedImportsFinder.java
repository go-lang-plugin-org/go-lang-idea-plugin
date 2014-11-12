package ro.redeul.google.go.imports;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.visitors.GoImportUsageCheckingVisitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UnusedImportsFinder {
    public static Collection<GoImportDeclaration> findUnusedImports(GoFile file) {

        Map<String, GoImportDeclaration> imports =
                new HashMap<String, GoImportDeclaration>();

        for (GoImportDeclarations importDeclarations : file.getImportDeclarations()) {
            for (GoImportDeclaration declaration : importDeclarations.getDeclarations()) {
                String visiblePackageName = declaration.getPackageAlias();
                if (!"".equals(visiblePackageName) && !"C".equals(visiblePackageName)) {
                    imports.put(visiblePackageName, declaration);
                }
            }
        }

        new GoImportUsageCheckingVisitor(imports).visitFile(file);

        return imports.values();
    }
}
