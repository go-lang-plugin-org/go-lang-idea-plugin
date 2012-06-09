package ro.redeul.google.go.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoImportUsageChecker;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/15/11
 * Time: 7:50 AM
 */
public class GoCodeManager {

    private static final Logger LOG = Logger.getInstance("ro.redeul.google.go.services.GoCodeManager");

    Project project;

    public GoCodeManager(Project project) {
        this.project = project;
    }

    public static GoCodeManager getInstance(Project project) {
        return ServiceManager.getService(project, GoCodeManager.class);
    }

    public Set<GoImportDeclaration> findUsedImports(GoFile file) {
        GoImportDeclarations[] declarations = file.getImportDeclarations();

        Set<GoImportDeclaration> usedImports = new HashSet<GoImportDeclaration>();

        for (GoImportDeclarations declaration : declarations) {
            GoImportDeclaration importSpecs[] = declaration.getDeclarations();

            for (GoImportDeclaration importSpec : importSpecs) {
                if ( isImportUsed(importSpec, file) ) {
                    usedImports.add(importSpec);
                }
            }
        }

        return usedImports;
    }

    public boolean isImportUsed(GoImportDeclaration importSpec, GoFile file) {
        GoImportUsageChecker importUsageChecker = new GoImportUsageChecker(importSpec);

        file.accept(importUsageChecker);

        return importUsageChecker.isUsed();
    }
}
