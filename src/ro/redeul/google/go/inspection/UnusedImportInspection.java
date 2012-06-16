package ro.redeul.google.go.inspection;

import java.util.Collection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.inspection.fix.RemoveImportFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.services.GoCodeManager;
import static ro.redeul.google.go.GoBundle.message;

public class UnusedImportInspection extends AbstractWholeGoFileInspection {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Unused import";
    }

    @Override
    protected void doCheckFile(@NotNull GoFile file,
                               @NotNull InspectionResult result,
                               boolean onTheFly) {
        Project project = file.getProject();

        PsiDocumentManager pdm = PsiDocumentManager.getInstance(project);
        Document document = pdm.getDocument(file);
        if (document != null) {
            pdm.commitDocument(document);
        }

        Collection<GoImportDeclaration> unusedImports =
            GoCodeManager.getInstance(project).findUnusedImports(file);

        for (GoImportDeclaration unused : unusedImports) {
            if (unused.getText().trim().isEmpty()) {
                continue;
            }

            result.addProblem(
                unused,
                message("warning.unused.import", unused.getImportPath()),
                ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                new RemoveImportFix(unused));
        }
    }
}
