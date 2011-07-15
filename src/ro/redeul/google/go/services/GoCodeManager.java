package ro.redeul.google.go.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.apache.xmlbeans.impl.xb.xsdschema.ImportDocument;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportSpec;
import ro.redeul.google.go.lang.psi.visitors.GoImportUsageChecker;

import java.util.Collections;
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

    public Set<GoImportSpec> findUsedImports(GoFile file) {
        GoImportDeclaration[] declarations = file.getImportDeclarations();

        Set<GoImportSpec> usedImports = new HashSet<GoImportSpec>();

        for (GoImportDeclaration declaration : declarations) {
            GoImportSpec importSpecs[] = declaration.getImports();

            for (GoImportSpec importSpec : importSpecs) {
                if ( isImportUsed(importSpec, file) ) {
                    usedImports.add(importSpec);
                }
            }
        }

        return usedImports;
    }

    private boolean isImportUsed(GoImportSpec importSpec, GoFile file) {
        GoImportUsageChecker importUsageChecker = new GoImportUsageChecker(importSpec);

        file.accept(importUsageChecker);

        return importUsageChecker.isUsed();
    }
}
