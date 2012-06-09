package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class ConvertToAssignmentFix implements LocalQuickFix {

    @NotNull
    @Override
    public String getName() {
        return "Convert to assignment";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Variable Declaration";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement e = descriptor.getStartElement();
        while ((e = e.getNextSibling()) != null) {
            if (":=".equals(e.getText())) {
                break;
            }
        }

        if (e == null) {
            return;
        }

        Document doc = PsiDocumentManager.getInstance(e.getProject()).getDocument(e.getContainingFile());
        doc.replaceString(e.getTextOffset(), e.getTextOffset() + e.getTextLength(), "=");
    }
}
