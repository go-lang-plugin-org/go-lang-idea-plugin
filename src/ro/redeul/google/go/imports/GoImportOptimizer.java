package ro.redeul.google.go.imports;

import com.intellij.lang.ImportOptimizer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.ide.GoProjectSettings;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportSpec;
import ro.redeul.google.go.services.GoCodeManager;

import java.util.Set;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/15/11
 * Time: 7:43 AM
 */
public class GoImportOptimizer implements ImportOptimizer {

    private static final Logger LOG = Logger.getInstance("#ro.redeul.google.go.imports.GoImportOptimizer");

    @Override
    public boolean supports(PsiFile file) {
        return file instanceof GoFile;
    }

    @NotNull
    @Override
    public Runnable processFile(PsiFile file) {
        if (!(file instanceof GoFile)) {
            return EmptyRunnable.getInstance();
        }

        final GoFile goFile = (GoFile) file;

        // go the enableOptimizeImports declaration and change it to true if you want to see it working.
        if ( ! GoProjectSettings.getInstance(goFile.getProject()).getState().enableOptimizeImports ) {
            return EmptyRunnable.getInstance();
        }

        return new Runnable() {
            @Override
            public void run() {
                Project project = goFile.getProject();

                PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
                Document document = manager.getDocument(goFile);
                if (document != null) {
                    manager.commitDocument(document);
                }

                GoCodeManager goCodeManager = GoCodeManager.getInstance(project);

                try {
                    Set<GoImportSpec> usedImports = goCodeManager.findUsedImports(goFile);

                    GoImportDeclaration[] importDeclarations = goFile.getImportDeclarations();

                    for (GoImportDeclaration importDeclaration : importDeclarations) {
                        GoImportSpec[] importSpecs = importDeclaration.getImports();

                        for (GoImportSpec importSpec : importSpecs) {
                            if (!usedImports.contains(importSpec)) {
                                importDeclaration.deleteChildRange(importSpec, importSpec);
                            }
                        }

                        if (importDeclaration.getImports().length == 0) {
                            goFile.deleteChildRange(importDeclaration, importDeclaration);
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Exception while optimizing go imports", ex);
                }
            }
        };
    }
}
