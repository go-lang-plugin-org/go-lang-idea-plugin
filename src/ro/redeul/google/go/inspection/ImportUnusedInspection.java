package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.imports.UnusedImportsFinder;
import ro.redeul.google.go.inspection.fix.RemoveImportFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;

import java.util.Collection;

import static ro.redeul.google.go.GoBundle.message;

public class ImportUnusedInspection extends AbstractWholeGoFileInspection {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Import Unused";
    }

    @Override
    protected void doCheckFile(@NotNull GoFile file,
                               @NotNull final InspectionResult result) {
        Project project = file.getProject();

        PsiDocumentManager pdm = PsiDocumentManager.getInstance(project);
        Document document = pdm.getDocument(file);
        if (document != null) {
            pdm.commitDocument(document);
        }

        Collection<GoImportDeclaration> unusedImports =
                UnusedImportsFinder.findUnusedImports(file);

        for (GoImportDeclaration unused : unusedImports) {
            if (!unused.isValidImport() || unused.getImportPath() == null) {
                continue;
            }

            result.addProblem(
                    unused,
                    message("warning.unused.import", unused.getImportPath().getValue()),
                    ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                    new RemoveImportFix(unused));
        }
    }
}
