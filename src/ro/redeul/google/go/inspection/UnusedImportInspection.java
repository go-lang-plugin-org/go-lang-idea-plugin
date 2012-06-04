package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.inspection.fix.RemoveImportFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.services.GoCodeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UnusedImportInspection extends AbstractGoInspection {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Unused import";
    }

    @Override
    protected ProblemDescriptor[] doCheckFile(@NotNull GoFile file, @NotNull InspectionManager manager) {
        List<ProblemDescriptor> problems = new ArrayList<ProblemDescriptor>();

        Project project = file.getProject();

        PsiDocumentManager pdm = PsiDocumentManager.getInstance(project);
        Document document = pdm.getDocument(file);
        if (document != null) {
            pdm.commitDocument(document);
        }

        Set<GoImportDeclaration> usedImports = GoCodeManager.getInstance(project).findUsedImports(file);
        for (GoImportDeclarations importDeclarations : file.getImportDeclarations()) {
            for (GoImportDeclaration id : importDeclarations.getDeclarations()) {
                RemoveImportFix fix = new RemoveImportFix();
                if (id.getText().trim().isEmpty() || usedImports.contains(id)) {
                    continue;
                }

                problems.add(manager.createProblemDescriptor(id, "Unused import", fix, ProblemHighlightType.ERROR, true));
            }
        }
        return problems.toArray(new ProblemDescriptor[problems.size()]);
    }
}
