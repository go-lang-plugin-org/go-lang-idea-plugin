package ro.redeul.google.go.imports;

import com.intellij.lang.ImportOptimizer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.inspection.fix.RemoveImportFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.services.GoCodeManager;

import java.util.Collection;

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

                Collection<GoImportDeclaration> unusedImports = goCodeManager.findUnusedImports(goFile);
                for (GoImportDeclaration imp : unusedImports) {
                    new RemoveImportFix(imp).invoke(project, goFile, null, imp, imp);
                }
            }
        };
    }
}
