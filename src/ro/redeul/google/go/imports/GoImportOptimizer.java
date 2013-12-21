package ro.redeul.google.go.imports;

import com.intellij.lang.ImportOptimizer;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.inspection.fix.RemoveImportFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.services.GoCodeManager;

import java.util.HashSet;
import java.util.Set;

import static ro.redeul.google.go.inspection.fix.FixUtil.removeWholeElement;

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
                optimize(goFile);
            }
        };
    }

    public static void optimize(final GoFile goFile) {
        final Project project = goFile.getProject();
        GoCodeManager goCodeManager = GoCodeManager.getInstance(project);
        final Set<GoImportDeclaration> unusedImports =
            new HashSet<GoImportDeclaration>(goCodeManager.findUnusedImports(goFile));

        for (GoImportDeclaration id : unusedImports) {
            GoLiteralString importPath = id.getImportPath();
            if (importPath == null) {
                return;
            }

            // refuse to optimize anything if there are some import whose path looks strange.
            String path = importPath.getValue();
            if (path.contains("\n") || path.contains(" ") || path.contains("\t")) {
                return;
            }
        }

        if (unusedImports.isEmpty()) {
            return;
        }

        new WriteCommandAction.Simple(goFile.getProject(), goFile) {
            @Override
            protected void run() throws Throwable {
                for (GoImportDeclarations ids : goFile.getImportDeclarations()) {
                    if (allImportsUnused(ids.getDeclarations(), unusedImports)) {
                        removeWholeElement(ids);
                    }
                }

                for (GoImportDeclaration imp : unusedImports) {
                    new RemoveImportFix(imp).invoke(project, goFile, null, imp, imp);
                }
            }
        }.execute();
    }

    private static boolean allImportsUnused(GoImportDeclaration[] declarations,
                                            Set<GoImportDeclaration> unusedImports) {
        for (GoImportDeclaration declaration : declarations) {
            if (!unusedImports.contains(declaration)) {
                return false;
            }
        }

        for (GoImportDeclaration declaration : declarations) {
            unusedImports.remove(declaration);
        }
        return true;
    }
}
